package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.ui.dto.PathRequest;
import wooteco.subway.ui.dto.PathResponse;
import wooteco.subway.util.PathAlgorithm;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineService lineService;
    private final StationService stationService;
    private final PathAlgorithm pathAlgorithm;

    public PathService(LineService lineService, StationService stationService, PathAlgorithm pathAlgorithm) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathAlgorithm = pathAlgorithm;
    }

    public PathResponse findShortestPath(PathRequest pathRequest) {
        List<Line> lines = lineService.findAllLines();
        Station source = stationService.findById(pathRequest.getSource());
        Station target = stationService.findById(pathRequest.getTarget());

        int distance = pathAlgorithm.calculateDistance(lines, source, target);
        Fare fare = Fare.from(distance, pathAlgorithm.getPassedLines(lines, source, target), pathRequest.getAge());
        return PathResponse.of(pathAlgorithm.getVertexes(lines, source, target), distance, fare.getAmount());
    }
}

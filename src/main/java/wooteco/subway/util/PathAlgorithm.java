package wooteco.subway.util;

import java.util.List;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Station;

public interface PathAlgorithm {

    int calculateDistance(List<Line> lines, Station source, Station target);

    Lines getPassedLines(List<Line> lines, Station source, Station target);

    List<Station> getVertexes(List<Line> lines, Station source, Station target);
}

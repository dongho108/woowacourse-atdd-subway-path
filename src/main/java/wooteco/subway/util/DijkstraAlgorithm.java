package wooteco.subway.util;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.ShortestPathEdge;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.EmptyResultException;

@Component
public class DijkstraAlgorithm implements PathAlgorithm {
    @Override
    public int calculateDistance(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = createGraph(lines, source, target);
        try {
            GraphPath<Station, ShortestPathEdge> path = findPath(source, target, graph);
            return (int) path.getWeight();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("해당 역을 찾지 못했습니다.");
        }
    }

    @Override
    public Lines getPassedLines(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = createGraph(lines, source, target);
        try {
            GraphPath<Station, ShortestPathEdge> path = findPath(source, target, graph);
            List<Line> passedLines = path.getEdgeList().stream()
                .map(ShortestPathEdge::getLine)
                .distinct()
                .collect(Collectors.toList());
            return new Lines(passedLines);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("해당 역을 찾지 못했습니다.");
        }
    }

    @Override
    public List<Station> getVertexes(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = createGraph(lines, source, target);
        try {
            GraphPath<Station, ShortestPathEdge> path = findPath(source, target, graph);
            return path.getVertexList();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("해당 역을 찾지 못했습니다.");
        }
    }

    private WeightedMultigraph<Station, ShortestPathEdge> createGraph(List<Line> lines,
        Station source, Station target) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);
        initGraph(lines, graph);

        validateSameStation(source, target);
        return graph;
    }

    private static void initGraph(List<Line> lines, WeightedMultigraph<Station, ShortestPathEdge> graph) {
        for (Line line : lines) {
            addVertex(graph, line.getStations());
        }

        for (Line line : lines) {
            addEdge(graph, line);
        }
    }

    private static void addVertex(WeightedMultigraph<Station, ShortestPathEdge> graph, List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void addEdge(WeightedMultigraph<Station, ShortestPathEdge> graph, Line line) {
        for (Section section : line.getSections()) {
            graph.addEdge(section.getUpStation(), section.getDownStation(),
                new ShortestPathEdge(line, section.getDistance()));
        }
    }

    private void validateSameStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }
    }

    private GraphPath<Station, ShortestPathEdge> findPath(Station source, Station target,
        WeightedMultigraph<Station, ShortestPathEdge> graph) {
        ShortestPathAlgorithm<Station, ShortestPathEdge> pathFinder = new DijkstraShortestPath<>(graph);
        GraphPath<Station, ShortestPathEdge> path = pathFinder.getPath(source, target);
        validateEmptyPath(path);
        return path;
    }

    private void validateEmptyPath(GraphPath<Station, ShortestPathEdge> path) {
        if (path == null) {
            throw new EmptyResultException("출발역과 도착역 사이에 연결된 경로가 없습니다.");
        }
    }
}

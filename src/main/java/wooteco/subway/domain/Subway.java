package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import wooteco.subway.domain.vo.Path;
import wooteco.subway.exception.EmptyResultException;

public class Subway {

    private final ShortestPathAlgorithm<Station, ShortestPathEdge> pathFinder;

    public Subway(ShortestPathAlgorithm<Station, ShortestPathEdge> pathFinder) {
        this.pathFinder = pathFinder;
    }

    public static Subway from(List<Line> lines) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);
        initGraph(lines, graph);

        return new Subway(new DijkstraShortestPath<>(graph));
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

    public Path findShortestPath(Station source, Station target) {
        validateSameStation(source, target);
        try {
            GraphPath<Station, ShortestPathEdge> path = pathFinder.getPath(source, target);
            validateEmptyPath(path);
            List<Line> lines = path.getEdgeList().stream()
                .map(ShortestPathEdge::getLine)
                .distinct()
                .collect(Collectors.toList());
            return Path.of(path.getVertexList(), path.getWeight(), new Lines(lines));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("?????? ?????? ?????? ???????????????.");
        }
    }

    private void validateSameStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("???????????? ???????????? ???????????????.");
        }
    }

    private void validateEmptyPath(GraphPath<Station, ShortestPathEdge> path) {
        if (path == null) {
            throw new EmptyResultException("???????????? ????????? ????????? ????????? ????????? ????????????.");
        }
    }
}

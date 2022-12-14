package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;

public class Line {
    private final Long id;
    private String name;
    private String color;
    private final Sections sections;
    private final int extraFare;

    private Line(Long id, String name, String color, Sections sections, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.extraFare = extraFare;
    }

    public Line(String name, String color) {
        this(null, name, color, null, 0);
    }

    public Line(String name, String color, int extraFare) {
        this(null, name, color, null, extraFare);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null, 0);
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(id, name, color, null, extraFare);
    }

    public static Line from(Line line, List<Section> sections) {
        return new Line(line.getId(), line.getName(), line.getColor(), Sections.from(sections), line.getExtraFare());
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void insertSection(Section section) {
        sections.insert(section);
    }

    public Long deleteSection(Station station) {
        return sections.delete(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections.getSections());
    }

    public int getExtraFare() {
        return extraFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Line line = (Line)o;

        return getName() != null ? getName().equals(line.getName()) : line.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}

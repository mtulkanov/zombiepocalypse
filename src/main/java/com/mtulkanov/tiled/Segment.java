package com.mtulkanov.tiled;

import lombok.Data;

@Data
public class Segment {

    private final Vector2 a;
    private final Vector2 b;

    public boolean intersect(Segment other) {
        Vector2 c = other.getA();
        Vector2 d = other.getB();

        Orientation o1 = orientation(a, b, c);
        Orientation o2 = orientation(a, b, d);
        Orientation o3 = orientation(c, d, a);
        Orientation o4 = orientation(c, d, b);

        if (o1 != o2 && o3 != o4) {
            return true;
        }
        return o1 == Orientation.COLINEAR && onSegment(c, this)
                || o2 == Orientation.COLINEAR && onSegment(d, this)
                || o3 == Orientation.COLINEAR && onSegment(a, other)
                || o4 == Orientation.COLINEAR && onSegment(b, other);
    }

    private Orientation orientation(Vector2 p1, Vector2 p2, Vector2 p3) {
        double val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX())
                - (p2.getX() - p1.getX()) * (p3.getY() - p2.getY());
        if (val == 0) {
            return Orientation.COLINEAR;
        } else if (val > 0) {
            return Orientation.CLOCKWISE;
        }
        return Orientation.COUNTERCLOCKWISE;
    }

    public static boolean onSegment(Vector2 point, Segment segment) {
        double minSegmentX = Math.min(segment.getA().getX(), segment.getB().getX());
        double maxSegmentX = Math.max(segment.getA().getX(), segment.getB().getX());
        double minSegmentY = Math.min(segment.getA().getY(), segment.getB().getY());
        double maxSegmentY = Math.max(segment.getA().getY(), segment.getB().getY());
        return (point.getX() >= minSegmentX && point.getX() <= maxSegmentX
            && point.getY() >= minSegmentY && point.getY() <= maxSegmentY);
    }

    public static void main(String[] args) {
        Segment s1 = new Segment(new Vector2(1248, 608), new Vector2(1305, 800.5));
        Segment s2 = new Segment(new Vector2(736, 608), new Vector2(736, 800));
        System.out.println(s2.intersect(s1));
    }

    private enum Orientation {
        COLINEAR,
        CLOCKWISE,
        COUNTERCLOCKWISE
    }
}

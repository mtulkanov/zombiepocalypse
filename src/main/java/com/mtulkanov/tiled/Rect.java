package com.mtulkanov.tiled;

import java.awt.*;

public class Rect {
    private Vector2 origin;
    private int width;
    private int height;

    public Rect(Vector2 firstPoint, int width, int height) {
        this.origin = new Vector2(
                firstPoint.getX() + width / 2.0,
                firstPoint.getY() + height / 2.0
        );
        this.width = width;
        this.height = height;
    }

    static Rect createFromOrigin(Vector2 origin, int width, int height) {
        var topLeft = new Vector2(
                origin.getX() - width / 2.0,
                origin.getY() - height / 2.0
        );
        return new Rect(topLeft, width, height);
    }

    Rect(Rect rect) {
        origin = new Vector2(rect.origin);
        width = rect.width;
        height = rect.height;
    }

    void translate(Vector2 v) {
        origin = origin.add(v);
    }

    @Override
    public String toString() {
        return String.format("Rect[origin=%s,width=%d,height=%d]",
                origin,
                width,
                height
        );
    }

    private Vector2[] getCorners() {
        var topLeft = new Vector2(
                getOrigin().getX() - width / 2.0,
                getOrigin().getY() - height / 2.0
        );
        var topRight = new Vector2(
                getOrigin().getX() + width / 2.0,
                getOrigin().getY() - height / 2.0
        );
        var bottomRight = new Vector2(
                getOrigin().getX() + width / 2.0,
                getOrigin().getY() + height / 2.0
        );
        var bottomLeft = new Vector2(
                getOrigin().getX() - width / 2.0,
                getOrigin().getY() + height / 2.0
        );
        return new Vector2[] {topLeft, topRight, bottomRight, bottomLeft};
    }

    public void draw(Graphics g, Color color, Vector2 offset) {
        var g2 = (Graphics2D) g.create();
        var savedColor = g2.getColor();
        g2.setColor(color);
        translate(offset);

        var corners = getCorners();
        g2.drawLine(corners[0].getIntX(), corners[0].getIntY(), corners[1].getIntX(), corners[1].getIntY());
        g2.drawLine(corners[1].getIntX(), corners[1].getIntY(), corners[2].getIntX(), corners[2].getIntY());
        g2.drawLine(corners[2].getIntX(), corners[2].getIntY(), corners[3].getIntX(), corners[3].getIntY());
        g2.drawLine(corners[3].getIntX(), corners[3].getIntY(), corners[0].getIntX(), corners[0].getIntY());

        translate(offset.negative());
        g2.setColor(savedColor);
        g2.dispose();
    }

    boolean collides(Rect rect) {
        var gapX = Math.abs(getOrigin().getX() - rect.getOrigin().getX())
                - rect.getWidth() / 2.0
                - width / 2.0;
        var gapY = Math.abs(getOrigin().getY() - rect.getOrigin().getY())
                - rect.getHeight() / 2.0
                - height / 2.0;
        return gapX < 0 && gapY < 0;
    }

    public Vector2 getTopLeft() {
        return new Vector2(
                getOrigin().getX() - width / 2.0,
                getOrigin().getY() - height / 2.0
        );
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean intersect(Segment segment) {
        Vector2[] corners = getCorners();
        Segment topBorder = new Segment(corners[0], corners[1]);
        Segment rightBorder = new Segment(corners[1], corners[2]);
        Segment bottomBorder = new Segment(corners[2], corners[3]);
        Segment leftBorder = new Segment(corners[3], corners[0]);
        return segment.intersect(topBorder)
                || segment.intersect(rightBorder)
                || segment.intersect(bottomBorder)
                || segment.intersect(leftBorder);
    }

    public boolean inside(Vector2 point) {
        Vector2[] corners = getCorners();
        Vector2 leftTop = corners[0];
        Vector2 bottomRight = corners[2];
        return point.getX() >= leftTop.getX() && point.getX() <= bottomRight.getX()
                && point.getY() >= leftTop.getY() && point.getY() <= bottomRight.getY();
    }
}

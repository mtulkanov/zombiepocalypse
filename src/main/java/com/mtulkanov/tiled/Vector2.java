package com.mtulkanov.tiled;

public class Vector2 {
    private double x, y;

    Vector2() {
        this(0, 0);
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2 add(Vector2 v) {
        var newX = x + v.getX();
        var newY = y + v.getY();
        return new Vector2(newX, newY);
    }

    public Vector2 minus(Vector2 v) {
        return new Vector2(x - v.getX(), y - v.getY());
    }

    Vector2 multi(double scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public Vector2 div(double scalar) {
        return new Vector2(x / scalar, y / scalar);
    }

    Vector2 rotate(double degrees) {
        var rad = Math.toRadians(degrees);
        var newX = Math.cos(rad) * x - Math.sin(rad) * y;
        var newY = Math.sin(rad) * x + Math.cos(rad) * y;
        return new Vector2(newX, newY);
    }

    public Vector2 negative() {
        return new Vector2(x * -1, y * -1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2)) {
            return false;
        }
        var v = (Vector2) obj;
        return x == v.getX() && y == v.getY();
    }

    Vector2 project(Vector2 v) {
        var unit = v.unit();
        return unit.multi(dotProduct(unit));
    }

    double dotProduct(Vector2 v) {
        return x * v.getX() + y * v.getY();
    }

    public Vector2 unit() {
        return new Vector2(x / magnitude(), y / magnitude());
    }

    double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    double angle(Vector2 v) {
        var dot = dotProduct(v);
        var det = x * v.getY() - y * v.getX();
        return -Math.toDegrees(Math.atan2(det, dot));
    }

    Vector2 normal() {
        return new Vector2(-y, x);
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }

    public int getIntX() {
        return (int) x;
    }

    public int getIntY() {
        return (int) y;
    }

    public double distance(Vector2 to) {
        return Math.sqrt(
                Math.pow((to.getX() - x), 2)
              + Math.pow((to.getY() - y), 2)
        );
    }

    @Override
    public String toString() {
        return String.format("Vector2[x=%.1f,y=%.1f]", x, y);
    }
}

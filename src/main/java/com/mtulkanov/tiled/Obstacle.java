package com.mtulkanov.tiled;

import java.awt.*;

public class Obstacle {
    private Rect rect;

    public Obstacle(Vector2 firstPoint, int width, int height) {
        rect = new Rect(firstPoint, width, height);
    }

    public Rect getRect() {
        return rect;
    }

    public void render(Graphics g, Vector2 offset) {
        rect.draw(g, Game.DEBUG_COLOR, offset);
    }
}

package com.mtulkanov.tiled.pathfinder;

import com.mtulkanov.tiled.Vector2;

import java.awt.*;

public interface Pathfinder {

    Vector2 path(Vector2 start, Vector2 stop);

    default void render(Graphics g, Vector2 offset) {
        throw new UnsupportedOperationException();
    }
}

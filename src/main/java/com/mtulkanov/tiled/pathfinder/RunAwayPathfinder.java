package com.mtulkanov.tiled.pathfinder;

import com.mtulkanov.tiled.Vector2;

import java.awt.*;

public class RunAwayPathfinder implements Pathfinder {

    @Override
    public Vector2 path(Vector2 start, Vector2 stop) {
        return stop.minus(start).unit().negative();
    }

    @Override
    public void render(Graphics g, Vector2 offset) {

    }
}

package com.mtulkanov.tiled.pathfinder;

import com.mtulkanov.tiled.Vector2;

public class StraightLinePathfinder implements Pathfinder {

    @Override
    public Vector2 path(Vector2 start, Vector2 stop) {
        return stop.minus(start).unit();
    }
}

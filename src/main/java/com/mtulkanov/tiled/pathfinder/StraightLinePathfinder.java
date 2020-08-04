package com.mtulkanov.tiled.pathfinder;

import com.mtulkanov.tiled.Game;
import com.mtulkanov.tiled.Vector2;

import java.awt.*;

public class StraightLinePathfinder implements Pathfinder {

    private Vector2 start;
    private Vector2 stop;

    @Override
    public Vector2 path(Vector2 start, Vector2 stop) {
        this.start = start;
        this.stop = stop;
        return stop.minus(start).unit();
    }

    @Override
    public void render(Graphics g, Vector2 offset) {
        Vector2 startOff = start.add(offset);
        Vector2 stopOff = stop.add(offset);
        g.setColor(Game.PATH_COLOR);
        g.drawLine(startOff.getIntX(), startOff.getIntY(), stopOff.getIntX(), stopOff.getIntY());
    }
}

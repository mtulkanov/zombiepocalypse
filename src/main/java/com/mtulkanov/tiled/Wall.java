package com.mtulkanov.tiled;

import java.awt.*;

class Wall {
    private Rect rect;

    Wall(Vector2 firstPoint) {
        rect = new Rect(firstPoint, Game.TILESIZE, Game.TILESIZE);
    }

    void render(Graphics g, Vector2 offset) {
        var renderPos = rect.getTopLeft().add(offset);
        g.drawImage(Assets.wall, renderPos.getIntX(), renderPos.getIntY(), Game.TILESIZE, Game.TILESIZE, null);
    }

    Rect getRect() {
        return rect;
    }
}

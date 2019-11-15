package com.mtulkanov.tiled;

import java.util.List;

class WallCollider {
    private List<Wall> walls;
    private Rect rect;

    WallCollider(Rect rect) {
        this.walls = Game.getGame().getWalls();
        this.rect = new Rect(rect);
    }

    Rect move(Vector2 displacement) {
        var beforeTrans = new Rect(rect);
        rect.translate(displacement);
        var collidesWall = collides();
        if (collidesWall == null) {
            return rect;
        }
        rect = new Rect(beforeTrans);
        var moveX = new Vector2(displacement.getX(), 0);
        rect.translate(moveX);
        var wallX = collides();
        if (wallX != null) {
            rect = new Rect(beforeTrans);
            double diffX = wallX.getRect().getOrigin().getX() - rect.getOrigin().getX();
            double gapX = Math.abs(diffX) - rect.getWidth() / 2.0 - wallX.getRect().getWidth() / 2.0;
            gapX = diffX > 0 ? gapX : -gapX;
            rect.translate(new Vector2(gapX, 0));
        }
        var moveY = new Vector2(0, displacement.getY());
        rect.translate(moveY);
        var wallY = collides();
        if (wallY != null) {
            double diffY = wallY.getRect().getOrigin().getY() - rect.getOrigin().getY();
            double gapY = Math.abs(diffY) - rect.getHeight() / 2.0 - wallY.getRect().getHeight() / 2.0;
            gapY = diffY > 0 ? gapY : -gapY;
            rect.translate(new Vector2(0, gapY));
        }
        return rect;
    }

    private Wall collides() {
        for (var wall: walls) {
            if (rect.collides(wall.getRect())) {
                return wall;
            }
        }
        return null;
    }
}

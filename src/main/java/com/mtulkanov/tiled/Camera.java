package com.mtulkanov.tiled;

class Camera {
    private int width;
    private int height;
    private Vector2 offset;

    Camera(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void update(Player target) {
        var x = -(target.getRect().getOrigin().getX()) + width / 2.0;
        var y = -(target.getRect().getOrigin().getY()) + height / 2.0;

        x = Math.min(x, 0);
        y = Math.min(y, 0);

        var maxXOffset = -(Game.getGame().getTileMap().getWidthPixels() - width);
        var maxYOffset = -(Game.getGame().getTileMap().getHeightPixels() - height);
        x = Math.max(x, maxXOffset);
        y = Math.max(y, maxYOffset);

        if (offset == null) {
            offset = new Vector2(x, y);
        } else {
            offset.setX(x);
            offset.setY(y);
        }
    }

    Vector2 getOffset() {
        return offset;
    }
}

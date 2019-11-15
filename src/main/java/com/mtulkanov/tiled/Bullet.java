package com.mtulkanov.tiled;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

class Bullet {
    private static final int MOVE_SPEED = 10;
    private static final int LIFETIME = 1_000_000_000;
    private static final int SPREAD = 5;
    private static final int DAMAGE = 10;

    private final Rect rect;
    private final BufferedImage sprite;
    private final Vector2 dir;
    private final long spawnTime;

    private boolean despawned = false;

    Bullet(Vector2 origin, Vector2 dir, BufferedImage sprite) {
        this.rect = Rect.createFromOrigin(origin, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
        this.dir = dir.rotate(getRandomSpread());
        this.spawnTime = System.nanoTime();
    }

    void update() {
        rect.translate(dir.multi(MOVE_SPEED));
        if (System.nanoTime() - spawnTime >= LIFETIME || collidesWithWalls()) {
            despawn();
        }
        for (var mob: Game.getGame().getMobs()) {
            if (mob.getRect().collides(rect)) {
                mob.takeDamage(DAMAGE);
                despawn();
            }
        }
    }

    void render(Graphics g, Vector2 offset) {
        var screenCoords = rect.getTopLeft().add(offset);
        g.drawImage(sprite, screenCoords.getIntX(), screenCoords.getIntY(), null);
    }

    void despawn() {
        despawned = true;
    }

    boolean isDespawned() {
        return despawned;
    }

    boolean collidesWithWalls() {
        for (var wall: Game.getGame().getWalls()) {
            if (rect.collides(wall.getRect())) {
                return true;
            }
        }
        return false;
    }

    double getRandomSpread() {
        var rng = new Random();
        return rng.nextDouble() * 10 - 5;
    }
}

package com.mtulkanov.tiled;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

class Mob {
    private final int[] MOVE_SPEEDS = {100, 125, 150};
    private final int AVOID_RADIUS = 50;
    private final int FULL_HEALTH = 100;
    private final int DAMAGE = 10;
    private final int KNOCKBACK = 20;
    private final long HIT_COOLDOWN = 500_000_000;

    private Rect rect;
    private BufferedImage sprite;
    private double rot;
    private int moveSpeed;
    private Vector2 vel = new Vector2(0, 0);
    private Vector2 acc = new Vector2(0, 0);
    private boolean dead = false;
    private int health = FULL_HEALTH;
    private long lastHit;

    Mob(Vector2 topLeft, BufferedImage sprite) {
        rect = new Rect(topLeft, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
        moveSpeed = getRandomSpeed();
    }

    void update() {
        var playerOrigin = Game.getGame().getPlayer().getRect().getOrigin();
        var sight = playerOrigin.minus(rect.getOrigin());
        rot = sight.angle(new Vector2(1, 0));
        acc = new Vector2(1, 0).rotate(rot);
        avoidMobs();
        acc = acc.multi(moveSpeed).add(vel.negative());
        vel = vel.add(acc.multi(Game.getGame().getDt()));
        var displacement =
                vel.multi(Game.getGame().getDt())
                .add(
                        acc
                                .multi(0.5)
                                .multi(Game.getGame().getDt() * Game.getGame().getDt())
                );
        if (System.nanoTime() - lastHit < HIT_COOLDOWN) {
            return;
        }
        rect = new WallCollider(rect).move(displacement);
        if (rect.collides(Game.getGame().getPlayer().getRect())) {
            Game.getGame().getPlayer().takeDamage(DAMAGE);
            lastHit = System.nanoTime();
            var knockback = new Vector2(KNOCKBACK, 0).rotate(rot);
            Game.getGame().getPlayer().knockback(knockback);
        }
    }

    private void avoidMobs() {
        for (Mob mob: Game.getGame().getMobs()) {
            if (mob != this) {
                var dir = rect.getOrigin().minus(mob.getRect().getOrigin());
                var distance = dir.magnitude();
                if (distance > 0 && distance < AVOID_RADIUS) {
                    acc = acc.add(dir.unit());
                }
            }
        }
    }

    void render(Graphics g, Vector2 offset) {
        var coords = rect.getOrigin().add(offset);
        drawSprite(g, coords);
        drawHealthBar(g, coords);
    }

    void drawSprite(Graphics g, Vector2 coords) {
        var g2 = (Graphics2D) g.create();
        g2.rotate(Math.toRadians(rot), coords.getIntX(), coords.getIntY());
        g2.drawImage(
                sprite,
                coords.getIntX() - rect.getWidth() / 2,
                coords.getIntY() - rect.getHeight() / 2,
                null
        );
        g2.dispose();
        g2.rotate(Math.toRadians(-rot), coords.getIntX(), coords.getIntY());
    }

    void drawHealthBar(Graphics g, Vector2 coords) {
        Color color = Color.GREEN;
        if (health < 60) {
            color = Color.YELLOW;
        }
        if (health < 30) {
            color = Color.RED;
        }
        if (health < FULL_HEALTH) {
            g.setColor(color);
            int width = rect.getWidth() * health / FULL_HEALTH;
            g.fillRect(coords.getIntX(), coords.getIntY(), width, 7);
        }
    }

    void kill() {
        dead = true;
    }

    Rect getRect() {
        return rect;
    }

    boolean isDead() {
        return dead;
    }

    private int getRandomSpeed() {
        var rng = new Random();
        var index = rng.nextInt(MOVE_SPEEDS.length);
        return MOVE_SPEEDS[index];
    }

    void takeDamage(int damage) {
        health -= damage;
        vel = new Vector2(0, 0);
        if (health <= 0) {
            kill();
        }
    }
}

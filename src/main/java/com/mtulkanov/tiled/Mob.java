package com.mtulkanov.tiled;

import com.mtulkanov.tiled.pathfinder.Pathfinder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

class Mob {
    private static final int[] MOVE_SPEEDS = {100, 125, 150};
    private static final int AVOID_RADIUS = 50;
    private static final int FULL_HEALTH = 100;
    private static final int DAMAGE = 10;
    private static final int KNOCKBACK = 20;
    private static final long HIT_COOLDOWN = 500_000_000;

    private Rect rect;
    private BufferedImage sprite;
    private double rot;
    private int moveSpeed;
    private Vector2 vel = new Vector2(0, 0);
    private Vector2 acc = new Vector2(0, 0);
    private boolean dead = false;
    private int health = FULL_HEALTH;
    private long lastHit;
    private Pathfinder pathfinder;

    public Mob(Vector2 topLeft, BufferedImage sprite, Pathfinder pathfinder) {
        this.rect = new Rect(topLeft, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
        this.moveSpeed = getRandomSpeed();
        this.pathfinder = pathfinder;
    }

    public void update(Player player) {
        var playerOrigin = player.getRect().getOrigin();
        var sight = pathfinder.path(rect.getOrigin(), playerOrigin);
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
        if (rect.collides(player.getRect())) {
            player.takeDamage(DAMAGE);
            lastHit = System.nanoTime();
            var knockback = new Vector2(KNOCKBACK, 0).rotate(rot);
            player.knockback(knockback);
        }
    }

    public void render(Graphics g, Vector2 offset) {
        var coords = rect.getOrigin().add(offset);
        drawSprite(g, coords);
        drawHealthBar(g, coords);
        if (Game.getGame().isDebug()) {
//            rect.draw(g, Game.DEBUG_COLOR, offset);
            pathfinder.render(g, offset);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        vel = new Vector2(0, 0);
        if (health <= 0) {
            kill();
        }
    }

    public void knockback(Vector2 knockback) {
        rect = new WallCollider(rect).move(knockback);
    }

    public Rect getRect() {
        return rect;
    }

    public boolean isDead() {
        return dead;
    }

    private int getRandomSpeed() {
        var rng = new Random();
        var index = rng.nextInt(MOVE_SPEEDS.length);
        return MOVE_SPEEDS[index];
    }

    private void kill() {
        dead = true;
    }

    private void drawSprite(Graphics g, Vector2 coords) {
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

    private void drawHealthBar(Graphics g, Vector2 coords) {
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

    private void avoidMobs() {
        for (Mob mob : Game.getGame().getMobs()) {
            if (mob != this) {
                var dir = rect.getOrigin().minus(mob.getRect().getOrigin());
                var distance = dir.magnitude();
                if (distance > 0 && distance < AVOID_RADIUS) {
                    acc = acc.add(dir.unit());
                }
            }
        }
    }
}

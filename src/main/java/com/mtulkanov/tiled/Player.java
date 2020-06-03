package com.mtulkanov.tiled;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

class Player {
    private static final int MOVE_SPEED = 200;
    private static final int ROTATION_SPEED = 250;
    private static final int FIRE_RATE = 150_000_000;
    private static final Vector2 BARREL_OFFSET = new Vector2(20, 10);
    private static final int KICKBACK = 3;
    private static final int FULL_HEALTH = 100;
    private static final long DAMAGED_DURATION = 250_000_000;
    private static final int HEALTHBAR_WIDTH = 100;
    private static final int HEALTHBAR_HEIGHT = 20;
    private static Logger log = Logger.getLogger(Player.class.getName());
    private Rect rect;
    private double rot;
    private long lastFire;
    private int health = FULL_HEALTH;
    private long lastHit;

    Player(Vector2 firstPoint, BufferedImage sprite) {
        rect = new Rect(firstPoint, sprite.getWidth(), sprite.getHeight());
    }

    void update() {
        var vel = new Vector2();
        double rot_speed = 0;
        if (Game.getGame().getKeyManager().left) {
            rot_speed += ROTATION_SPEED;
        }
        if (Game.getGame().getKeyManager().right) {
            rot_speed -= ROTATION_SPEED;
        }
        rot += -rot_speed * Game.getGame().getDt();
        if (Game.getGame().getKeyManager().up) {
            vel = new Vector2(MOVE_SPEED * Game.getGame().getDt(), 0).rotate(rot);
        }
        if (Game.getGame().getKeyManager().down) {
            vel = new Vector2(-MOVE_SPEED * Game.getGame().getDt() / 2, 0).rotate(rot);
        }

        if (canFire()) {
            fire(new Vector2(1, 0).rotate(rot));
            vel = vel.add(new Vector2(-KICKBACK, 0).rotate(rot));
        }
        rect = new WallCollider(rect).move(vel);
    }

    void render(Graphics g, Vector2 offset) {
        var coords = rect.getOrigin().add(offset);
        drawSprite(g, coords);
        drawHealthBar(g);
        if (Game.getGame().isDebug()) {
            rect.draw(g, Game.DEBUG_COLOR, offset);
        }
    }

    void drawSprite(Graphics g, Vector2 coords) {
        var g2 = (Graphics2D)g.create();
        g2.rotate(Math.toRadians(rot), coords.getIntX(), coords.getIntY());
        var sprite = Assets.player;
        if (System.nanoTime() - lastHit < DAMAGED_DURATION) {
            sprite = Assets.playerDamaged;
        }
        g2.drawImage(sprite, coords.getIntX() - rect.getWidth() / 2, coords.getIntY() - rect.getHeight() / 2, null);
        g2.dispose();
        g2.rotate(Math.toRadians(-rot), coords.getIntX(), coords.getIntY());
    }

    void drawHealthBar(Graphics g) {
        var color = Color.GREEN;
        if (health < 60) {
            color = Color.YELLOW;
        }
        if (health < 30) {
            color = Color.RED;
        }
        var x = 10;
        var y = 10;
        g.setColor(Color.WHITE);
        var g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));
        g.drawRect(x, y, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT);
        g.setColor(color);
        var innerWidth = (int)(1.0 * health / FULL_HEALTH * HEALTHBAR_WIDTH);
        g.fillRect(x, y, innerWidth, HEALTHBAR_HEIGHT);
    }

    boolean canFire() {
        var canFire = Game.getGame().getKeyManager().space && System.nanoTime() - lastFire >= FIRE_RATE;
        if (canFire) {
            lastFire = System.nanoTime();
        }
        return canFire;
    }

    void fire(Vector2 dir) {
        var bullet = new Bullet(rect.getOrigin().add(BARREL_OFFSET.rotate(rot)), dir, Assets.bullet);
        Game.getGame().getBullets().add(bullet);
    }

    void takeDamage(int damage) {
        health -= damage;
        lastHit = System.nanoTime();
        if (health <= 0) {
            Game.getGame().reset();
        }
    }

    void knockback(Vector2 displacement) {
        rect = new WallCollider(rect).move(displacement);
    }

    Rect getRect() {
        return rect;
    }
}

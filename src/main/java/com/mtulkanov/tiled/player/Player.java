package com.mtulkanov.tiled.player;

import com.mtulkanov.tiled.*;
import com.mtulkanov.tiled.observer.Event;
import com.mtulkanov.tiled.observer.PlayerMovedEvent;
import com.mtulkanov.tiled.observer.Subject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends GameObject {
    public static final Vector2 BARREL_OFFSET = new Vector2(20, 10);
    public static final int FULL_HEALTH = 100;

    private static final long DAMAGED_DURATION = 250_000_000;
    private Rect rect;
    private double rot;
    private Vector2 vel;
    private int health = FULL_HEALTH;
    private long lastHit;

    private InputComponent inputComponent;
    private GraphicsComponent graphicsComponent;

    private Subject subject;

    public Player(
            Vector2 firstPoint,
            BufferedImage sprite,
            InputComponent inputComponent,
            GraphicsComponent graphicsComponent,
            Subject subject
    ) {
        rect = new Rect(firstPoint, sprite.getWidth(), sprite.getHeight());
        this.vel = new Vector2();

        this.inputComponent = inputComponent;
        this.graphicsComponent = graphicsComponent;
        subject.register(inputComponent);
        subject.register(graphicsComponent);

        this.subject = subject;
    }

    public void update(KeyManager keyManager, double dt, Graphics g, Vector2 offset) {
        inputComponent.update(this, keyManager, dt);
        rect = new WallCollider(rect).move(vel);

        Event playerMovedEvent = new PlayerMovedEvent(rect.getOrigin());

        graphicsComponent.render(this, g, offset);
        this.vel = new Vector2();
    }

    public void takeDamage(int damage) {
        health -= damage;
        lastHit = System.nanoTime();
        if (health <= 0) {
            Game.getGame().reset();
        }
    }

    public void knockback(Vector2 displacement) {
        rect = new WallCollider(rect).move(displacement);
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public double getRot() {
        return rot;
    }

    public void setRot(double rot) {
        this.rot = rot;
    }

    public Vector2 getVel() {
        return vel;
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public void notify(Event event) {
        subject.notify(event, this);
    }

    public int getHealth() {
        return health;
    }
}

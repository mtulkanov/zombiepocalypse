package com.mtulkanov.tiled.player;

import com.mtulkanov.tiled.GameObject;
import com.mtulkanov.tiled.KeyManager;
import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.observer.Event;
import com.mtulkanov.tiled.observer.EventType;
import com.mtulkanov.tiled.observer.Observer;

public class InputComponent implements Observer {

    private static final int KICKBACK = 3;
    private static final int MOVE_SPEED = 200;
    private static final int ROTATION_SPEED = 250;

    public void update(Player player, KeyManager keyManager, double dt) {
        rotate(player, keyManager, dt);
        speed(player, keyManager, dt);
    }

    private void rotate(Player player, KeyManager keyManager, double dt) {
        double rotSpeed = 0;
        if (keyManager.left) {
            rotSpeed += ROTATION_SPEED;
        }
        if (keyManager.right) {
            rotSpeed -= ROTATION_SPEED;
        }
        double newRot = player.getRot() + -rotSpeed * dt;
        player.setRot(newRot);
    }

    private void speed(Player player, KeyManager keyManager, double dt) {
        Vector2 newVel = new Vector2();
        if (keyManager.up) {
            newVel = new Vector2(MOVE_SPEED * dt, 0).rotate(player.getRot());
        }
        if (keyManager.down) {
            newVel = new Vector2(-MOVE_SPEED * dt / 2, 0).rotate(player.getRot());
        }
        newVel = newVel.add(player.getVel());
        player.setVel(newVel);
    }

    @Override
    public void update(Event event, GameObject gameObject) {
        Player player = (Player) gameObject;
        if (event.getType() == EventType.FIRE) {
            Vector2 kickback = new Vector2(-KICKBACK, 0).rotate(player.getRot());
            Vector2 newVel = player.getVel().add(kickback);
            player.setVel(newVel);
        }
    }
}

package com.mtulkanov.tiled.system;

import com.mtulkanov.tiled.Assets;
import com.mtulkanov.tiled.Bullet;
import com.mtulkanov.tiled.KeyManager;
import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.observer.Event;
import com.mtulkanov.tiled.observer.EventType;
import com.mtulkanov.tiled.player.Player;

import java.util.List;

public class BattleSystem {

    private static final int FIRE_RATE = 150;

    private long lastFire;

    public void update(Player player, List<Bullet> bullets, KeyManager keyManager) {
        if (bulletFired(keyManager)) {
            fire(player, bullets);
        }
    }

    private boolean bulletFired(KeyManager keyManager) {
        return keyManager.space && System.currentTimeMillis() - lastFire >= FIRE_RATE;
    }

    private void fire(Player player, List<Bullet> bullets) {
        Bullet bullet = createBullet(player);
        bullets.add(bullet);
        sendFireEvent(player);
    }

    private void sendFireEvent(Player player) {
        Event fireEvent = new Event(EventType.FIRE);
        player.notify(fireEvent);
        lastFire = System.currentTimeMillis();
    }

    private Bullet createBullet(Player player) {
        Vector2 fireDirection = new Vector2(1, 0).rotate(player.getRot());
        Vector2 armOffset = Player.BARREL_OFFSET.rotate(player.getRot());
        Vector2 bulletPos = player.getRect().getOrigin().add(armOffset);
        return new Bullet(bulletPos, fireDirection, Assets.bullet);
    }
}

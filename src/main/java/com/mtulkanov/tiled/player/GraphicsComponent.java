package com.mtulkanov.tiled.player;

import com.mtulkanov.tiled.Assets;
import com.mtulkanov.tiled.Game;
import com.mtulkanov.tiled.GameObject;
import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.observer.Event;
import com.mtulkanov.tiled.observer.Observer;

import java.awt.*;

public class GraphicsComponent implements Observer {

    private static final int HEALTHBAR_WIDTH = 100;
    private static final int HEALTHBAR_HEIGHT = 20;

    public void render(Player player, Graphics g, Vector2 offset) {
        var coords = player.getRect().getOrigin().add(offset);
        drawSprite(player, g, coords);
        drawHealthBar(player, g);
        if (Game.getGame().isDebug()) {
            player.getRect().draw(g, Game.DEBUG_COLOR, offset);
        }
    }

    void drawSprite(Player player, Graphics g, Vector2 coords) {
        var g2 = (Graphics2D)g.create();
        g2.rotate(Math.toRadians(player.getRot()), coords.getIntX(), coords.getIntY());
        var sprite = Assets.player;
//        if (System.nanoTime() - lastHit < DAMAGED_DURATION) {
//            sprite = Assets.playerDamaged;
//        }
        g2.drawImage(sprite, coords.getIntX() - player.getRect().getWidth() / 2, coords.getIntY() - player.getRect().getHeight() / 2, null);
        g2.dispose();
        g2.rotate(Math.toRadians(-player.getRot()), coords.getIntX(), coords.getIntY());
    }

    void drawHealthBar(Player player, Graphics g) {
        var color = Color.GREEN;
        if (player.getHealth() < 60) {
            color = Color.YELLOW;
        }
        if (player.getHealth() < 30) {
            color = Color.RED;
        }
        var x = 10;
        var y = 10;
        g.setColor(Color.WHITE);
        var g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));
        g.drawRect(x, y, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT);
        g.setColor(color);
        var innerWidth = (int)(1.0 * player.getHealth() / Player.FULL_HEALTH * HEALTHBAR_WIDTH);
        g.fillRect(x, y, innerWidth, HEALTHBAR_HEIGHT);
    }

    @Override
    public void update(Event event, GameObject gameObject) {
        Player player = (Player) gameObject;
    }
}

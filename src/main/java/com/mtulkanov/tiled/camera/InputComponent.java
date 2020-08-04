package com.mtulkanov.tiled.camera;

import com.mtulkanov.tiled.Game;
import com.mtulkanov.tiled.GameObject;
import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.observer.Event;
import com.mtulkanov.tiled.observer.Observer;
import com.mtulkanov.tiled.observer.PlayerMovedEvent;

public class InputComponent implements Observer {

    @Override
    public void update(Event event, GameObject gameObject) {
        Camera camera = (Camera) gameObject;
        if (event instanceof PlayerMovedEvent) {
            PlayerMovedEvent playerMovedEvent = (PlayerMovedEvent) event;
            playerMovedHandler(camera, playerMovedEvent.getPlayerPos());
        }
    }

    private void playerMovedHandler(Camera camera, Vector2 playerPos) {
        double x = -(playerPos.getX()) + camera.getWidth() / 2.0;
        double y = -(playerPos.getY()) + camera.getHeight() / 2.0;

        x = Math.min(x, 0);
        y = Math.min(y, 0);

        double maxXOffset = -(Game.getGame().getTileMap().getWidthPixels() - camera.getWidth());
        double maxYOffset = -(Game.getGame().getTileMap().getHeightPixels() - camera.getHeight());

        x = Math.max(x, maxXOffset);
        y = Math.max(y, maxYOffset);

        Vector2 newOffset = new Vector2(x, y);
        camera.setOffset(newOffset);
    }
}

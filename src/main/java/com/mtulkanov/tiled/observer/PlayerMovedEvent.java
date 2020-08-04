package com.mtulkanov.tiled.observer;

import com.mtulkanov.tiled.Vector2;
import lombok.Getter;

@Getter
public class PlayerMovedEvent extends Event {

    private final Vector2 playerPos;

    public PlayerMovedEvent(Vector2 playerPos) {
        super(EventType.PLAYER_MOVED);
        this.playerPos = playerPos;
    }
}

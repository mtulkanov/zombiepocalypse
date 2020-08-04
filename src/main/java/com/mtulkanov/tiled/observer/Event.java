package com.mtulkanov.tiled.observer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Event {

    private final EventType type;
}

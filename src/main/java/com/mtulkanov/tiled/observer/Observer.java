package com.mtulkanov.tiled.observer;

import com.mtulkanov.tiled.GameObject;

public interface Observer {

    void update(Event event, GameObject gameObject);
}

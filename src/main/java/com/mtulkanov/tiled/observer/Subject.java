package com.mtulkanov.tiled.observer;

import com.mtulkanov.tiled.GameObject;

public interface Subject {

    void notify(Event event, GameObject gameObject);

    void register(Observer observer);
}

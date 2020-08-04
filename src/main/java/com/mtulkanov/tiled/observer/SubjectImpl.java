package com.mtulkanov.tiled.observer;

import com.mtulkanov.tiled.GameObject;

import java.util.ArrayList;
import java.util.List;

public class SubjectImpl implements Subject {

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void notify(Event event, GameObject gameObject) {
        observers.forEach(observer -> observer.update(event, gameObject));
    }

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }
}

package com.mtulkanov.tiled.camera;

import com.mtulkanov.tiled.GameObject;
import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.observer.Subject;

public class Camera extends GameObject {
    private Vector2 offset;
    private int width;
    private int height;

    private final InputComponent inputComponent;
    private final Subject subject;

    public Camera(int width, int height, InputComponent inputComponent, Subject subject) {
        this.width = width;
        this.height = height;
        this.inputComponent = inputComponent;
        this.subject = subject;
        subject.register(inputComponent);
    }

    public Vector2 getOffset() {
        return offset;
    }

    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

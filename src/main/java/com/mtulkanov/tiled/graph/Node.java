package com.mtulkanov.tiled.graph;

import com.mtulkanov.tiled.Vector2;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node {

    @EqualsAndHashCode.Include
    private final String id;
    private final Vector2 pos;

    public Node(String id, Vector2 pos) {
        this.id = id;
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public Vector2 getPos() {
        return pos;
    }
}

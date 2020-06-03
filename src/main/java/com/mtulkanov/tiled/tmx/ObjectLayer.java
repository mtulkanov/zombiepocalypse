package com.mtulkanov.tiled.tmx;

import lombok.Data;

import java.util.Map;

@Data
public class ObjectLayer {

    private final Map<String, TileObject> objects;
}

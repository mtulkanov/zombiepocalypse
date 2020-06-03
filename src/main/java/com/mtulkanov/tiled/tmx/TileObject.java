package com.mtulkanov.tiled.tmx;

import lombok.Data;

@Data
public class TileObject {

    private final String id;
    private final int x;
    private final int y;

    private String name;
    private String type;
    private int width;
    private int height;
}

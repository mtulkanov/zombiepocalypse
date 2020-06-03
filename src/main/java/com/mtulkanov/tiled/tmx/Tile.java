package com.mtulkanov.tiled.tmx;

import com.mtulkanov.tiled.Rect;
import lombok.Data;

@Data
public class Tile {

    private final int gid;
    private final Rect rect;
    private final boolean flippedHorizontally;
    private final boolean flippedVertically;
    private final boolean flippedDiagonally;
}

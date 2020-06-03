package com.mtulkanov.tiled.tmx;

import lombok.Data;

import java.util.List;

@Data
public class Layer {

    private final List<Tile> tiles;
}

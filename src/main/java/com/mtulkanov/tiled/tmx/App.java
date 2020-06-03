package com.mtulkanov.tiled.tmx;

public class App {

    public static void main(String[] args) {
        TileMap tileMap = new TileMap("maps/test.tmx");
        tileMap.toString();
    }
}

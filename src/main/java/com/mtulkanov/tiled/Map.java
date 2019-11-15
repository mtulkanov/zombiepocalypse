package com.mtulkanov.tiled;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class Map {
    private final ArrayList<String> rows;
    private final int width;
    private final int height;

    Map(String filename) {
        this.rows = new ArrayList<>();
        readFile(filename);
        var tileWidth = rows.get(0).length();
        var tileHeight = rows.size();
        this.width = tileWidth * Game.TILESIZE;
        this.height = tileHeight * Game.TILESIZE;
    }

    private void readFile(String filename) {
        try (var reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filename)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Wall> getWalls() {
        var walls = new ArrayList<Wall>();
        for (int y = 0; y < rows.size(); y++) {
            var row = rows.get(y);
            for (int x = 0; x < rows.get(y).length(); x++) {
                if (row.charAt(x) == '1') {
                    var wall = new Wall(new Vector2(x * Game.TILESIZE, y * Game.TILESIZE));
                    walls.add(wall);
                }
            }
        }
        return walls;
    }

    Player getPlayer() {
        for (int y = 0; y < rows.size(); y++) {
            var row = rows.get(y);
            for (int x = 0; x < rows.get(y).length(); x++) {
                if (row.charAt(x) == 'P') {
                    return new Player(new Vector2(x * Game.TILESIZE, y * Game.TILESIZE), Assets.player);
                }
            }
        }
        return new Player(new Vector2(0, 0), Assets.player);
    }

    List<Mob> getMobs() {
        List<Mob> mobs = new ArrayList<>();
        for (int y = 0; y < rows.size(); y++) {
            var row = rows.get(y);
            for (int x = 0; x < row.length(); x++) {
                if (row.charAt(x) == 'M') {
                    var mob = new Mob(new Vector2(x * Game.TILESIZE, y * Game.TILESIZE), Assets.mob);
                    mobs.add(mob);
                }
            }
        }
        return mobs;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }
}

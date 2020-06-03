package com.mtulkanov.tiled.graph;

import com.mtulkanov.tiled.Vector2;
import com.mtulkanov.tiled.tmx.TileMap;
import com.mtulkanov.tiled.tmx.TileObject;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class NodeLoader {

    public static List<Node> load(TileMap tileMap) {
        List<TileObject> nodeObjects = tileMap.getTileObjects().get("Node");
        List<Node> nodes = new ArrayList<>();
        for (TileObject nodeObject : nodeObjects) {
            String id = nodeObject.getId();
            int x = nodeObject.getX();
            int y = nodeObject.getY();
            Vector2 pos = new Vector2(x, y);
            Node node = new Node(id, pos);
            nodes.add(node);
        }
        return nodes;
    }
}

package com.mtulkanov.tiled.graph;

import com.mtulkanov.tiled.Game;
import com.mtulkanov.tiled.Segment;
import com.mtulkanov.tiled.Vector2;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Edge {

    private final Node a;
    private final Node b;

    public void render(Graphics g, Vector2 offset, Color color) {
        Vector2 aOff = a.getPos().add(offset);
        Vector2 bOff = b.getPos().add(offset);
        g.setColor(color);
        g.drawLine(aOff.getIntX(), aOff.getIntY(), bOff.getIntX(), bOff.getIntY());
    }

    public static List<Edge> nodesToEdges(List<Node> nodes) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            Node edgeStart = nodes.get(i);
            Node edgeEnd = nodes.get(i + 1);
            Edge edge = new Edge(edgeStart, edgeEnd);
            edges.add(edge);
        }
        return edges;
    }

    public Segment segment() {
        return new Segment(a.getPos(), b.getPos());
    }
}

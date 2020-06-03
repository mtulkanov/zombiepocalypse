package com.mtulkanov.tiled.pathfinder;

import com.mtulkanov.tiled.*;
import com.mtulkanov.tiled.graph.*;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class BreadthFirstSearchPathfinder implements Pathfinder {

    private final Graph fixedNodes;
    private final BreadthFirstSearchWithPath bfs;
    private final List<Obstacle> obstacles;
    private Graph nodes;
    private List<Node> path;

    @Override
    public Vector2 path(Vector2 start, Vector2 stop) {
        nodes = new Graph(fixedNodes);

        Node startNode = new Node("start", start);
        addNode(startNode);

        Node stopNode = new Node("stop", stop);
        addNode(stopNode);

        path = bfs.path(startNode, stopNode, nodes);
        Node nextVisibleNode = path.get(1);

        return nextVisibleNode.getPos().minus(start).unit();
    }

    private void addNode(Node node) {
        List<Node> visibleFromStartNodes = getVisibleNodesFor(node);
        nodes.addNodeWithAdjacentNodes(node, visibleFromStartNodes);
    }

    private List<Node> getVisibleNodesFor(Node start) {
        List<Node> visibleNodes = new ArrayList<>();
        for (Node node : nodes.getAdjacentNodes().keySet()) {
            if (isNodeVisibleFrom(node, start)) {
                visibleNodes.add(node);
            }
        }
        return visibleNodes;
    }

    private boolean isNodeVisibleFrom(Node nodeToCheck, Node from) {
        Segment segment = new Segment(nodeToCheck.getPos(), from.getPos());
        for (Edge edge : nodes.getEdges()) {
            Segment other = new Segment(edge.getA().getPos(), edge.getB().getPos());
            if (nodeToCheck.equals(edge.getA()) || nodeToCheck.equals(edge.getB())) {
                continue;
            }
            if (segment.intersect(other)) {
                return false;
            }
        }
        for (Obstacle obstacle: obstacles) {
            if (obstacle.getRect().intersect(segment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void render(Graphics g, Vector2 offset) {
        for (Edge edge : nodes.getEdges()) {
            edge.render(g, offset, Game.NODE_COLOR);
        }
        for (Edge edge: Edge.nodesToEdges(path)) {
            edge.render(g, offset, Game.PATH_COLOR);
        }
    }
}

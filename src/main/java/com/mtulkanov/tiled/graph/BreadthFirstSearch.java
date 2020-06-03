package com.mtulkanov.tiled.graph;

import lombok.Data;

import java.util.*;
import java.util.function.BiConsumer;

@Data
public class BreadthFirstSearch {

    private final Map<Node, List<Node>> nodes;
    private Set<Node> visitedNodes;

    public void reset() {
        visitedNodes = new HashSet<>();
    }

    public void breadthFirstSearch(Node node, BiConsumer<Node, Node> task) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node currentNode = queue.remove();

            if (visitedNodes.contains(currentNode)) {
                continue;
            }
            visitedNodes.add(currentNode);

            List<Node> adjacentNodes = this.nodes.get(currentNode);
            for (Node adjacentNode : adjacentNodes) {
                if (!visitedNodes.contains(adjacentNode)) {
                    queue.add(adjacentNode);
                    task.accept(currentNode, adjacentNode);
                }
            }
        }
    }
}

package com.mtulkanov.tiled.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Data
public class Graph {

    private final Map<Node, List<Node>> adjacentNodes;
    private final BreadthFirstSearch bfs;

    public Graph() {
        this.adjacentNodes = new HashMap<>();
        this.bfs = new BreadthFirstSearch(adjacentNodes);
    }

    public Graph(Graph copy) {
        this.adjacentNodes = new HashMap<>();
        for (Node node : copy.getAdjacentNodes().keySet()) {
            List<Node> adjacentCopy = copy.getAdjacentNodes().get(node);
            addNodeWithAdjacentNodes(node, new ArrayList<>(adjacentCopy));
        }
        this.bfs = new BreadthFirstSearch(adjacentNodes);
    }

    public void addNodeWithAdjacentNodes(Node node, List<Node> nodes) {
        adjacentNodes.put(node, nodes);
        for (Node adjacentNode : nodes) {
            if (!adjacentNodes.containsKey(adjacentNode)) {
                adjacentNodes.put(adjacentNode, new ArrayList<>());
            }
            List<Node> newAdjacentNodes = adjacentNodes.get(adjacentNode);
            if (!newAdjacentNodes.contains(node)) {
                newAdjacentNodes.add(node);
            }
        }
    }

    public void addEdge(Node from, Node to) {
        if (!adjacentNodes.containsKey(from)) {
            adjacentNodes.put(from, new ArrayList<>());
        }
        if (!adjacentNodes.containsKey(to)) {
            adjacentNodes.put(to, new ArrayList<>());
        }
        adjacentNodes.get(from).add(to);
        adjacentNodes.get(to).add(from);
    }

    public Node getRandomNode() {
        return adjacentNodes.keySet().iterator().next();
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        BiConsumer<Node, Node> collectEdges = (nodeA, nodeB) -> {
            Edge edge = new Edge(nodeA, nodeB);
            edges.add(edge);
        };
        doTask(collectEdges);
        return edges;
    }

    public void doTask(BiConsumer<Node, Node> task) {
        bfs.reset();
        bfs.breadthFirstSearch(getRandomNode(), task);
    }
}

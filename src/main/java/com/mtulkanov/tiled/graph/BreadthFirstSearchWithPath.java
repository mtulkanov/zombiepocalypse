package com.mtulkanov.tiled.graph;

import java.util.*;
import java.util.stream.Collectors;

public class BreadthFirstSearchWithPath {

    public List<Node> path(Node start, Node goal, Graph graph) {
        Map<Node, Node> cameFrom = cameFrom(start, goal, graph);
        return path(start, goal, cameFrom);
    }

    private Map<Node, Node> cameFrom(Node start, Node goal, Graph graph) {
        Queue<DistanceNode> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(DistanceNode::getDistance)
        );
        DistanceNode startDistanced = new DistanceNode(start, 0);
        frontier.add(startDistanced);

        Map<DistanceNode, DistanceNode> cameFrom = new HashMap<>();
        cameFrom.put(startDistanced, startDistanced);

        Map<DistanceNode, Double> costSoFar = new HashMap<>();
        costSoFar.put(startDistanced, 0.0);

        while (!frontier.isEmpty()) {
            DistanceNode current = frontier.remove();
            if (current.getNode().equals(goal)) {
                break;
            }
            for (DistanceNode next : getAdjacentDistanceNodesFor(current, graph)) {
                double newCost = costSoFar.get(current) + current.getDistance();
                if (!cameFrom.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
        }
        return convertDistanceNodeMapToNodeMap(cameFrom);
    }

    private Map<Node, Node> convertDistanceNodeMapToNodeMap(Map<DistanceNode, DistanceNode> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getNode(),
                        e -> e.getValue() == null ? null : e.getValue().getNode()
                ));
    }

    private List<Node> path(Node start, Node goal, Map<Node, Node> cameFrom) {
        List<Node> path = new ArrayList<>();
        Node current = goal;
        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    private List<DistanceNode> getAdjacentDistanceNodesFor(DistanceNode distanceNode, Graph graph) {
        return graph.getAdjacentNodes().get(distanceNode.getNode()).stream()
                .map(adjacentNode -> DistanceNode.getDistanceNodeFrom(distanceNode, adjacentNode))
                .collect(Collectors.toList());
    }
}

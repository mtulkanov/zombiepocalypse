package com.mtulkanov.tiled.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DistanceNode implements Comparable<DistanceNode> {

    @EqualsAndHashCode.Include
    private final Node node;
    private final double distance;

    public double computeDistance(Node otherNode) {
        return node.getPos().distance(otherNode.getPos());
    }

    @Override
    public int compareTo(@NotNull DistanceNode o) {
        if (distance < o.distance) {
            return -1;
        } else if (distance == o.distance) {
            return 0;
        }
        return 1;
    }

    public static DistanceNode getDistanceNodeFrom(DistanceNode from, Node to) {
        double distance = from.getNode().getPos().distance(to.getPos());
        return new DistanceNode(to, distance);
    }
}

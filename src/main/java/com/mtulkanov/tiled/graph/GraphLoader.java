package com.mtulkanov.tiled.graph;

import com.mtulkanov.tiled.Obstacle;
import com.mtulkanov.tiled.Segment;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class GraphLoader {

    public static Graph load(List<Node> nodes, List<Obstacle> walls) {
        Graph graph = new Graph();
        for (Node start : nodes) {
            for (Node stop : nodes) {
                if (stop.equals(start)) {
                    continue;
                }
                Segment segment = new Segment(start.getPos(), stop.getPos());
                if (isSegmentUninterruptedByWalls(segment, walls) && noNodesOnSegment(segment, nodes)) {
                    graph.addEdge(start, stop);
                }
            }
        }
        return graph;
    }

    private static boolean isSegmentUninterruptedByWalls(Segment segment, List<Obstacle> walls) {
        for (Obstacle wall : walls) {
            if (wall.getRect().intersect(segment)) {
                return false;
            }
        }
        return true;
    }

    private static boolean noNodesOnSegment(Segment segment, List<Node> nodes) {
        for (Node node : nodes) {
            if (!node.getPos().equals(segment.getA())
                    && !node.getPos().equals(segment.getB())
                    && Segment.onSegment(node.getPos(), segment)) {
                return false;
            }
        }
        return true;
    }
}

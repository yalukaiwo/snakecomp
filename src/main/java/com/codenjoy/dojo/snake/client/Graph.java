package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

public class Graph {

    public static List<Vertex> getPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }

    public static void computeShortestPaths(Vertex source) {
        source.minDistance = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            for (Edge adjEdge : current.edges) {
                Vertex neighbour = adjEdge.target();
                double edgeWeight = adjEdge.weight();
                double distanceThroughCurrent = current.minDistance + edgeWeight;
                if (distanceThroughCurrent < neighbour.minDistance) {
                    neighbour.minDistance = distanceThroughCurrent;
                    neighbour.previous = current;
                    neighbour.direction = adjEdge.action();
                    queue.offer(neighbour);
                }
            }
        }
    }

    public static Pair<Vertex, Vertex> getGraph(Board board, int[] start, int[] target, HashMap<Integer, Integer> customValues) {
        Vertex source = new Vertex(start, 0);
        Vertex targetVertex = null;
        HashMap<Integer, Vertex> visited = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            visited.put(Arrays.hashCode(current.coordinates), current);

            if (target[0] == current.coordinates[0] && target[1] == current.coordinates[1]) {
                targetVertex = current;
            }

            if (current.coordinates[0] < 14) {
                Edge e = makeConnectingEdge(board, visited, queue, customValues, new int[]{current.coordinates[0] + 1, current.coordinates[1]}, Direction.RIGHT);
                current.edges.add(e);
            }

            if (current.coordinates[0] > 0) {
                Edge e = makeConnectingEdge(board, visited, queue, customValues, new int[]{current.coordinates[0] - 1, current.coordinates[1]}, Direction.LEFT);
                current.edges.add(e);
            }

            if (current.coordinates[1] < 14) {
                Edge e = makeConnectingEdge(board, visited, queue, customValues, new int[]{current.coordinates[0], current.coordinates[1] + 1}, Direction.UP);
                current.edges.add(e);
            }

            if (current.coordinates[1] > 0) {
                Edge e = makeConnectingEdge(board, visited, queue, customValues, new int[]{current.coordinates[0], current.coordinates[1] - 1}, Direction.DOWN);
                current.edges.add(e);
            }
        }

        return new Pair<>(source, targetVertex);
    }

    private static Edge makeConnectingEdge(Board board, HashMap<Integer, Vertex> visited, PriorityQueue<Vertex> queue, HashMap<Integer, Integer> customValues, int[] coordinates, Direction direction) {
        Vertex v = visited.get(Arrays.hashCode(coordinates));
        if (v != null) {
            return new Edge(v, v.price, direction);
        } else {
            Elements element = board.getAt(coordinates[0], coordinates[1]);
            int price = element.equals(Elements.BAD_APPLE) ? 10 : element.equals(Elements.GOOD_APPLE) || element.equals(Elements.NONE) ? 1 : 1000;
            if (customValues.containsKey(Arrays.hashCode(coordinates)))
                price = customValues.get(Arrays.hashCode(coordinates));
            v = new Vertex(coordinates, price);
            visited.put(Arrays.hashCode(v.coordinates), v);
            queue.offer(v);
            return new Edge(v, price, direction);
        }
    }

    public static List<Vertex> getPath(Board board, Point source, Point target, HashMap<Integer, Integer> customValues) {
        return getPath(board, new int[]{source.getX(), source.getY()}, new int[]{target.getX(), target.getY()}, customValues);
    }

    public static List<Vertex> getPath(Board board, int[] source, int[] target, HashMap<Integer, Integer> customValues) {
        Pair<Vertex, Vertex> graph = getGraph(board, source, target, customValues);
        Vertex first = graph.first();
        Vertex second = graph.second();
        computeShortestPaths(first);
        return getPathTo(second);
    }

}

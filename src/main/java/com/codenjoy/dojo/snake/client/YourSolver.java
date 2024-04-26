package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

/**
 * User: Luka Ponomarenko
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.minPrevious) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }


    private static void computePaths(Vertex source) {
        source.minDistance = 0;
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            for (Edge adjEdge : current.edges) {
                Vertex neighbour = adjEdge.target();
                double edgeWeight = adjEdge.minWeight();
                double distanceThroughCurrent = current.minDistance + edgeWeight;
                if (distanceThroughCurrent < neighbour.minDistance) {
                    neighbour.minDistance = distanceThroughCurrent;
                    neighbour.minPrevious = current;
                    neighbour.direction = adjEdge.action;
                    queue.offer(neighbour);
                }
            }
        }
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                "http://165.22.23.49/codenjoy-contest/board/player/w2yuir5eqcz40udkgmp7?code=7996639318638999092",
                new YourSolver(new RandomDice()),
                new Board());
    }

    private Pair<Vertex, Vertex> getGraph(Board board, int[] target, int bodyIgnored) {
        Vertex source = new Vertex(board.getHead(), 0);
        Vertex targetVertex = null;
        HashMap<Integer, Vertex> visited = new HashMap<>();
        PriorityQueue<Vertex> q = new PriorityQueue<>();
        q.offer(source);
        List<int[]> ignored = board.getSnake().subList(board.getSnake().size() - bodyIgnored, board.getSnake().size()).stream().map(item -> new int[]{item.getX(), item.getY()}).toList();

        while (!q.isEmpty()) {
            Vertex current = q.poll();
            visited.put(Arrays.hashCode(current.coordinates), current);

            if (target[0] == current.coordinates[0] && target[1] == current.coordinates[1]) {
                targetVertex = current;
            }

            if (current.coordinates[0] < 14) {
                Vertex v = visited.get(Arrays.hashCode(new int[]{current.coordinates[0] + 1, current.coordinates[1]}));
                if (v != null) {
                    Edge e = new Edge(v, v.price, 1000 - v.price, Direction.RIGHT);
                    current.edges.add(e);
                } else {
                    Elements element = board.getAt(current.coordinates[0] + 1, current.coordinates[1]);
                    int price = element.equals(Elements.BAD_APPLE) ? 10 : element.equals(Elements.GOOD_APPLE) || element.equals(Elements.NONE) || isTail(element) ? 1 : 1000;
                    Vertex v1 = new Vertex(current.coordinates[0] + 1, current.coordinates[1], price);
                    visited.put(Arrays.hashCode(v1.coordinates), v1);
                    Edge e = new Edge(v1, price, 1000 - price, Direction.RIGHT);
                    current.edges.add(e);
                    q.offer(v1);
                }
            }

            if (current.coordinates[0] > 0) {
                Vertex v = visited.get(Arrays.hashCode(new int[]{current.coordinates[0] - 1, current.coordinates[1]}));
                if (v != null) {
                    Edge e = new Edge(v, v.price, Direction.LEFT);
                    current.edges.add(e);
                } else {
                    Elements element = board.getAt(current.coordinates[0] - 1, current.coordinates[1]);
                    int price = element.equals(Elements.BAD_APPLE) ? 10 : element.equals(Elements.GOOD_APPLE) || element.equals(Elements.NONE) || isTail(element) ? 1 : 1000;
                    Vertex v1 = new Vertex(current.coordinates[0] - 1, current.coordinates[1], price);
                    visited.put(Arrays.hashCode(v1.coordinates), v1);
                    Edge e = new Edge(v1, price, Direction.LEFT);
                    current.edges.add(e);
                    q.offer(v1);
                }
            }

            if (current.coordinates[1] < 14) {
                Vertex v = visited.get(Arrays.hashCode(new int[]{current.coordinates[0], current.coordinates[1] + 1}));
                if (v != null) {
                    Edge e = new Edge(v, v.price, Direction.UP);
                    current.edges.add(e);
                } else {
                    Elements element = board.getAt(current.coordinates[0], current.coordinates[1] + 1);
                    int price = element.equals(Elements.BAD_APPLE) ? 10 : element.equals(Elements.GOOD_APPLE) || element.equals(Elements.NONE) || isTail(element) ? 1 : 1000;
                    Vertex v1 = new Vertex(current.coordinates[0], current.coordinates[1] + 1, price);
                    visited.put(Arrays.hashCode(v1.coordinates), v1);
                    Edge e = new Edge(v1, price, Direction.UP);
                    current.edges.add(e);
                    q.offer(v1);
                }
            }

            if (current.coordinates[1] > 0) {
                Vertex v = visited.get(Arrays.hashCode(new int[]{current.coordinates[0], current.coordinates[1] - 1}));
                if (v != null) {
                    Edge e = new Edge(v, v.price, Direction.DOWN);
                    current.edges.add(e);
                } else {
                    Elements element = board.getAt(current.coordinates[0], current.coordinates[1] - 1);
                    int price = element.equals(Elements.BAD_APPLE) ? 10 : element.equals(Elements.GOOD_APPLE) || element.equals(Elements.NONE) || isTail(element) ? 1 : 1000;
                    Vertex v1 = new Vertex(current.coordinates[0], current.coordinates[1] - 1, price);
                    visited.put(Arrays.hashCode(v1.coordinates), v1);
                    Edge e = new Edge(v1, price, Direction.DOWN);
                    current.edges.add(e);
                    q.offer(v1);
                }
            }
        }

        return new Pair<>(source, targetVertex);
    }

    private boolean isTail(Elements e) {
        return e.equals(Elements.TAIL_END_UP) || e.equals(Elements.TAIL_END_LEFT) || e.equals(Elements.TAIL_END_DOWN) || e.equals(Elements.TAIL_END_RIGHT);
    }

    private Pair<Vertex, Vertex> getGraph(Board board, int[] target) {
        return getGraph(board, target, 0);
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) return Direction.UP.toString();

        Pair<Vertex, Vertex> graphToApple = getGraph(board, new int[]{board.getApples().get(0).getX(), board.getApples().get(0).getY()});
        Vertex source = graphToApple.first;
        Vertex apple = graphToApple.second;
        computePaths(source);
        List<Vertex> path = getShortestPathTo(apple);

        if (path.get(path.size() - 1).minDistance > 1000) {
            System.out.println("FOLLOWING TAIL!");

            Point snakeTail = null;

            for (Point p : board.getSnake()) {
                if (isTail(board.getAt(p))) {
                    snakeTail = p;
                }
            }

            assert snakeTail != null;
            Pair<Vertex, Vertex> graphToTail = getGraph(board, new int[]{snakeTail.getX(), snakeTail.getY()});
            Vertex head = graphToTail.first;
            Vertex tail = graphToTail.second;
            computePaths(head);
            List<Vertex> pathToTail = getShortestPathTo(tail);

            return pathToTail.get(1).direction.toString();
        }

        return path.get(1).direction.toString();
    }

    record Pair<A, B>(A first, B second) {
    }

    public static class Vertex implements Comparable<Vertex> {
        public final int[] coordinates;
        public final int price;
        public final List<Edge> edges = new ArrayList<>();
        public Direction direction = Direction.UP;
        public double minDistance = Double.POSITIVE_INFINITY;
        public double maxDistance = 0;
        public Vertex minPrevious;
        public Vertex maxPrevious;

        public Vertex(int[] coordinates, int price) {
            this.coordinates = coordinates;
            this.price = price;
        }

        public Vertex(int x, int y, int price) {
            this.coordinates = new int[]{x, y};
            this.price = price;
        }

        public Vertex(Point point, int price) {
            this.coordinates = new int[]{point.getX(), point.getY()};
            this.price = price;
        }

        @Override
        public int compareTo(Vertex o) {
            return Double.compare(minDistance, o.minDistance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (this.getClass() != o.getClass()) return false;
            Vertex v = (Vertex) o;
            return v.coordinates[0] == coordinates[0] && v.coordinates[1] == coordinates[1];
        }
    }

    public record Edge(Vertex target, double minWeight, double maxWeight, Direction action) {
    }

}

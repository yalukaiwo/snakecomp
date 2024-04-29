package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex> {
    public final int[] coordinates;
    public final int price;
    public final List<Edge> edges = new ArrayList<>();
    public Direction direction = Direction.UP;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public Vertex(int x, int y, int price) {
        this.coordinates = new int[]{x, y};
        this.price = price;
    }

    public Vertex(int[] coordinates, int price) {
        this.coordinates = coordinates;
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


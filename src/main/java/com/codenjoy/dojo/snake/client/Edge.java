package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;

public record Edge(Vertex target, double weight, Direction action) {
}

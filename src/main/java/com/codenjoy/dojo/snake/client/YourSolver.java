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

    private HashMap<Integer, Integer> getCustomPointValues(Board board) {
        HashMap<Integer, Integer> values = new HashMap<>();
        List<Point> snakeOrdered = new ArrayList<>();
        Snake.orderSnake(board, board.getHead(), board.getSnakeDirection(), snakeOrdered);

        Point head = board.getHead();

        for (int i = 0; i < snakeOrdered.size(); i++) {
            Point point = snakeOrdered.get(i);
            int distanceToHead = Math.abs(head.getX() - point.getX()) + Math.abs(head.getY() - point.getY());
            int pointLifetime = snakeOrdered.size() - i;
            if (distanceToHead >= pointLifetime) {
                values.put(Arrays.hashCode(new int[]{point.getX(), point.getY()}), 1);
            }

        }

        return values;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) return Direction.UP.toString();

        HashMap<Integer, Integer> customPointValues = getCustomPointValues(board);

        List<Vertex> path = Graph.getPath(board, board.getHead(), board.getApples().get(0), customPointValues);

        return path.get(1).direction.toString();
    }
    public static void main(String[] args) {
        WebSocketRunner.runClient(
                "http://165.22.23.49/codenjoy-contest/board/player/w2yuir5eqcz40udkgmp7?code=7996639318638999092",
                new YourSolver(new RandomDice()),
                new Board());
    }
}

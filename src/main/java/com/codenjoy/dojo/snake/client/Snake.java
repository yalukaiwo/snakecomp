package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.List;

public class Snake {
    public static boolean isHead(Elements element) {
        return element.equals(Elements.HEAD_DOWN) || element.equals(Elements.HEAD_UP) || element.equals(Elements.HEAD_RIGHT) || element.equals(Elements.HEAD_LEFT);
    }

    public static boolean isTail(Elements element) {
        return element.equals(Elements.TAIL_END_UP) || element.equals(Elements.TAIL_END_LEFT) || element.equals(Elements.TAIL_END_DOWN) || element.equals(Elements.TAIL_END_RIGHT);
    }

    public static boolean isSnake(Elements element) {
        return isTail(element) || isHead(element) || isBody(element);
    }

    public static boolean isBody(Elements element) {
        return element.equals(Elements.TAIL_VERTICAL) || element.equals(Elements.TAIL_HORIZONTAL) || element.equals(Elements.TAIL_LEFT_DOWN) || element.equals(Elements.TAIL_LEFT_UP) || element.equals(Elements.TAIL_RIGHT_DOWN) || element.equals(Elements.TAIL_RIGHT_UP);
    }

    public static Direction getNextDirection(Board board, Elements element, Direction previousDirection) {

        if (isHead(element)) return board.getSnakeDirection();
        if (isTail(element)) return board.getSnakeDirection();

        if (element.equals(Elements.TAIL_VERTICAL)) return previousDirection;
        if (element.equals(Elements.TAIL_HORIZONTAL)) return previousDirection;

        if (element.equals(Elements.TAIL_LEFT_DOWN) && previousDirection.equals(Direction.LEFT)) return Direction.UP;
        if (element.equals(Elements.TAIL_LEFT_DOWN) && previousDirection.equals(Direction.DOWN)) return Direction.RIGHT;
        if (element.equals(Elements.TAIL_LEFT_UP) && previousDirection.equals(Direction.LEFT)) return Direction.DOWN;
        if (element.equals(Elements.TAIL_LEFT_UP) && previousDirection.equals(Direction.UP)) return Direction.RIGHT;

        if (element.equals(Elements.TAIL_RIGHT_DOWN) && previousDirection.equals(Direction.RIGHT)) return Direction.UP;
        if (element.equals(Elements.TAIL_RIGHT_DOWN) && previousDirection.equals(Direction.DOWN)) return Direction.LEFT;
        if (element.equals(Elements.TAIL_RIGHT_UP) && previousDirection.equals(Direction.RIGHT)) return Direction.DOWN;
        if (element.equals(Elements.TAIL_RIGHT_UP) && previousDirection.equals(Direction.UP)) return Direction.LEFT;

        return Direction.UP;
    }

    public static void orderSnake(Board board, Point current, Direction direction, List<Point> result) {
        result.add(current.copy());

        if (isTail(board.getAt(current))) return;

        switch (direction) {
            case UP:
                Elements under = board.getAt(current.getX(), current.getY() - 1);
                if (isSnake(under)) {
                    current.setY(current.getY() - 1);
                    orderSnake(board, current, getNextDirection(board, under, direction), result);
                }
                break;
            case DOWN:
                Elements above = board.getAt(current.getX(), current.getY() + 1);
                if (isSnake(above)) {
                    current.setY(current.getY() + 1);
                    orderSnake(board, current, getNextDirection(board, above, direction), result);
                }
                break;
            case LEFT:
                Elements right = board.getAt(current.getX() + 1, current.getY());
                if (isSnake(right)) {
                    current.setX(current.getX() + 1);
                    orderSnake(board, current, getNextDirection(board, right, direction), result);
                }
                break;
            case RIGHT:
                Elements left = board.getAt(current.getX() - 1, current.getY());
                if (isSnake(left)) {
                    current.setX(current.getX() - 1);
                    orderSnake(board, current, getNextDirection(board, left, direction), result);
                }
        }
    }

}

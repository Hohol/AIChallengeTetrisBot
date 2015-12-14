package tetris.logic;

import tetris.Board;
import tetris.Move;
import tetris.TetriminoWithPosition;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static tetris.Move.*;
import static tetris.Move.ROTATE_CCW;

class PathFinder {
    static Move getPrevMove(TetriminoWithPosition cur, TetriminoWithPosition prev) {
        Move prevMove;
        if (prev.moveLeft().equals(cur)) {
            prevMove = LEFT;
        } else if (prev.moveRight().equals(cur)) {
            prevMove = RIGHT;
        } else if (prev.moveDown().equals(cur)) {
            prevMove = DOWN;
        } else if (prev.rotateCW().equals(cur)) {
            prevMove = ROTATE_CW;
        } else if (prev.rotateCCW().equals(cur)) {
            prevMove = ROTATE_CCW;
        } else if (prev.equals(cur)) {
            return null;
        } else {
            throw new RuntimeException("no move transforms " + prev + " to " + cur);
        }
        return prevMove;
    }

    static TetriminoWithPosition[][][] bfs(Board board, TetriminoWithPosition t) {
        TetriminoWithPosition[][][] from = new TetriminoWithPosition[board.getHeight()][board.getWidth()][t.getTetrimino().getOrientationsCnt()];
        from[t.getTopRow()][t.getLeftCol()][t.getTetrimino().getOrientation()] = t;
        Queue<TetriminoWithPosition> q = new ArrayDeque<>();
        q.add(t);
        while (!q.isEmpty()) {
            t = q.remove();
            List<TetriminoWithPosition> nextPositions = new ArrayList<>(); // todo not recreate?
            nextPositions.add(t.rotateCW());
            nextPositions.add(t.rotateCCW());
            nextPositions.add(t.moveLeft());
            nextPositions.add(t.moveRight());
            nextPositions.add(t.moveDown());
            for (TetriminoWithPosition p : nextPositions) {
                if (board.collides(p)) {
                    continue;
                }
                if (from[p.getTopRow()][p.getLeftCol()][p.getTetrimino().getOrientation()] != null) {
                    continue;
                }
                from[p.getTopRow()][p.getLeftCol()][p.getTetrimino().getOrientation()] = t;
                q.add(p);
            }
        }
        return from;
    }
}

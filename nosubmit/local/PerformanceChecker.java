package local;

import tetris.*;
import tetris.logic.BestMoveFinder;

import java.util.List;
import java.util.Random;

public class PerformanceChecker {
    public static void main(String[] args) {
        int seed = new Random().nextInt();
        System.out.println("seed = " + seed);
        double avgMoveCnt = getAvgMoveCnt(new Random(seed));
        System.out.println("avgMoveCnt = " + avgMoveCnt);
    }

    private static double getAvgMoveCnt(Random rnd) {
        double sum = 0;
        int STEP_CNT = 100;
        int failCnt = 0;
        for (int i = 0; i < STEP_CNT; i++) {
            try {
                sum += getMoveCnt(rnd);
            } catch (Exception ignored) {
                System.out.println("fail");
                failCnt++;
            }
        }
        System.out.println("failCnt = " + failCnt);
        return sum / STEP_CNT;
    }

    private static int getMoveCnt(Random rnd) {
        BestMoveFinder bestMoveFinder = new BestMoveFinder();
        Board board = new Board(Board.STANDARD_HEIGHT, Board.STANDARD_WIDTH);
        for (int row = 9; row < board.getHeight(); row++) {
            int emptyCol = rnd.nextInt(board.getWidth());
            for (int col = 0; col < board.getWidth(); col++) {
                if (col != emptyCol) {
                    board.set(row, col, true);
                }
            }
        }
        log(board);
        TetriminoType cur = getRandomTetrimino(rnd);
        TetriminoType next = getRandomTetrimino(rnd);
        int cnt = 0;
        while (board.getMaxColumnHeight() > 3) {
            log(cur);
            cnt++;
            TetriminoWithPosition fallingTetrimino = board.newFallingTetrimino(cur);
            List<Move> moves = bestMoveFinder.findBestMoves(new GameState(board, fallingTetrimino, next, 0));
            for (Move move : moves) {
                fallingTetrimino = fallingTetrimino.move(move, board);
            }
            board = board.drop(fallingTetrimino).getBoard();
            log(board);
            cur = next;
            next = getRandomTetrimino(rnd);
        }
        return cnt;
    }

    private static void log(Object o) {
        //System.out.println(o);
    }

    private static TetriminoType getRandomTetrimino(Random rnd) {
        return TetriminoType.values()[rnd.nextInt(TetriminoType.values().length)];
    }
}

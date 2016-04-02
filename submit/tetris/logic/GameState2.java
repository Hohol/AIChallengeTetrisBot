package tetris.logic;

import tetris.Board;
import tetris.TetriminoType;
import tetris.TetriminoWithPosition;

import java.util.List;

public class GameState2 {
    final Board board;
    final TetriminoType nextTetrimino;
    final TetriminoWithPosition fallingTetrimino;
    final int score;
    final int combo;
    final int round;
    final int skipCnt;
    final List<Integer> possibleGarbage;
    final double prevStateEval;
    final int linesCleared;

    public GameState2(Board board, TetriminoWithPosition fallingTetrimino, TetriminoType nextTetrimino, int score, int combo, int round, double prevStateEval, int skipCnt, List<Integer> possibleGarbage, int linesCleared) {
        this.board = board;
        this.nextTetrimino = nextTetrimino;
        this.fallingTetrimino = fallingTetrimino;
        this.score = score;
        this.combo = combo;
        this.round = round;
        this.skipCnt = skipCnt;
        this.possibleGarbage = possibleGarbage;
        this.prevStateEval = prevStateEval;
        this.linesCleared = linesCleared;
    }
}

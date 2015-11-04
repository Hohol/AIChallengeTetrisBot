package local;

import tetris.*;
import tetris.logic.BestMoveFinder;

import java.util.List;

class FullGameState {
    Board board;
    int score;
    int combo;
    boolean lost;
    int garbageSentOnLastMove;
    int round = 1;
    int skipCnt;
    int garbageAcceptedSum;

    FullGameState(Board board) {
        this.board = board;
    }

    public void makeMove(TetriminoType curTetrimino, TetriminoType nextTetrimino, BestMoveFinder player) {
        if (lost) {
            throw new RuntimeException();
        }
        TetriminoWithPosition fallingTetrimino = board.newFallingTetrimino(curTetrimino);
        if (board.collides(fallingTetrimino)) {
            lost = true;
            return;
        }
        List<Move> moves = player.findBestMoves(new GameState(board, fallingTetrimino, nextTetrimino, combo, round, skipCnt));
        DropResult dropResult = board.moveAndDrop(fallingTetrimino, moves, combo, round);
        board = dropResult.getBoard();
        combo = dropResult.getCombo();
        int newScore = score + dropResult.getScoreAdded();
        garbageSentOnLastMove = newScore / 3 - score / 3;
        score = newScore;
        round++;
        if (board.getMaxColumnHeight() == board.getHeight()) {
            lost = true;
        }
        skipCnt += dropResult.getSkipAdded();
    }

    public void addGarbage(Holes... holes) {
        if (lost) {
            throw new RuntimeException();
        }
        garbageAcceptedSum += holes.length;
        board.addGarbage(holes);
        if (board.getMaxColumnHeight() == board.getHeight()) {
            lost = true;
        }
    }
}

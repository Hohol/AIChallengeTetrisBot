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
        DropResult dropResult;
        if (!moves.isEmpty() && moves.get(0) == Move.SKIP) {
            dropResult = board.skipMove(combo, round);
        } else {
            for (Move move : moves) {
                fallingTetrimino = fallingTetrimino.move(move, board);
            }
            Move lastMove = moves.isEmpty() ? null : moves.get(moves.size() - 1);
            if (!board.collides(fallingTetrimino.moveDown())) {
                fallingTetrimino = fallingTetrimino.move(Move.DROP, board);
                lastMove = Move.DROP;
            }
            dropResult = board.drop(fallingTetrimino, lastMove, combo, round);
        }

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

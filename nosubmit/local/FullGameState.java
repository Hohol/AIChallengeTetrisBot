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
        List<Move> moves = player.findBestMoves(new GameState(board, fallingTetrimino, nextTetrimino, combo, round));
        for (Move move : moves) {
            fallingTetrimino = fallingTetrimino.move(move, board);
        }
        Move lastMove = moves.isEmpty() ? null : moves.get(moves.size() - 1);
        if (!board.collides(fallingTetrimino.moveDown())) {
            fallingTetrimino = fallingTetrimino.move(Move.DROP, board);
            lastMove = Move.DROP;
        }
        DropResult dropResult = board.drop(fallingTetrimino, lastMove, combo, round);
        board = dropResult.getBoard();
        if (dropResult.getScoreAdded() > 0) {
            combo++;
        } else {
            combo = 0;
        }
        int newScore = score + dropResult.getScoreAdded();
        garbageSentOnLastMove = newScore / 4 - score / 4;
        score = newScore;
        round++;
        if (board.getMaxColumnHeight() == board.getHeight()) {
            lost = true;
        }
    }

    public void addGarbage(int... emptyCols) {
        if (lost) {
            throw new RuntimeException();
        }
        board.addGarbage(emptyCols);
        if (board.getMaxColumnHeight() == board.getHeight()) {
            lost = true;
        }
    }
}

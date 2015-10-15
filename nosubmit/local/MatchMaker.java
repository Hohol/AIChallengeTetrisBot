package local;

import tetris.*;
import tetris.logic.BestMoveFinder;

import java.util.List;
import java.util.Random;

import static local.MatchResult.*;

public class MatchMaker {
    public MatchResult playMatch(BestMoveFinder firstPlayer, BestMoveFinder secondPlayer) {
        FullGameState firstGameState = new FullGameState(new Board(Board.STANDARD_HEIGHT, Board.STANDARD_WIDTH));
        FullGameState secondGameState = new FullGameState(new Board(Board.STANDARD_HEIGHT, Board.STANDARD_WIDTH));
        Random rnd = new Random();
        TetriminoType curTetrimino = getRandomTetrimino(rnd);
        TetriminoType nextTetrimino = getRandomTetrimino(rnd);

        while (true) {
            int firstLinesGenerated = firstGameState.makeMove(firstPlayer, curTetrimino, nextTetrimino);
            int secondLinesGenerated = secondGameState.makeMove(secondPlayer, curTetrimino, nextTetrimino);
            firstGameState.addGarbage(secondLinesGenerated);
            secondGameState.addGarbage(firstLinesGenerated);

            if (firstGameState.lost && secondGameState.lost) {
                return DRAW;
            }
            if (firstGameState.lost) {
                return SECOND_WON;
            }
            if (secondGameState.lost) {
                return FIRST_WON;
            }

            curTetrimino = nextTetrimino;
            nextTetrimino = getRandomTetrimino(rnd);
        }
    }

    static class FullGameState {
        Board board;
        int score;
        int combo;
        boolean lost;

        FullGameState(Board board) {
            this.board = board;
        }

        public int makeMove(BestMoveFinder player, TetriminoType curTetrimino, TetriminoType nextTetrimino) { // returns number of garbage lines sent to opponent
            TetriminoWithPosition fallingTetrimino = board.newFallingTetrimino(curTetrimino);
            List<Move> moves = player.findBestMoves(new GameState(board, fallingTetrimino, nextTetrimino, combo));

            return 0;
        }

        public void addGarbage(int secondLinesGenerated) {

        }
    }

    private static TetriminoType getRandomTetrimino(Random rnd) {
        return TetriminoType.values()[rnd.nextInt(TetriminoType.values().length)];
    }
}

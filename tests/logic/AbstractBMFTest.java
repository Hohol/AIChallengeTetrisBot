package logic;

import tetris.*;
import tetris.logic.Action;
import tetris.logic.BestMoveFinder;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static tetris.Move.*;

public class AbstractBMFTest {
    protected BestMoveFinder bestMoveFinder;
    BestMoveFinderTest.TestBuilder testBuilder;

    public static Board newBoard(String s) {
        String[] a = s.split("\n");
        if (a.length < Board.STANDARD_HEIGHT) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < a[0].length(); i++) {
                b.append(".");
            }
            b.append("\n");
            String empty = b.toString();
            b.setLength(0);
            for (int i = 0; i < Board.STANDARD_HEIGHT - a.length; i++) {
                b.append(empty);
            }
            b.append(s);
            s = b.toString();
        } else if (a.length > Board.STANDARD_HEIGHT) {
            throw new RuntimeException("non-standard board height");
        }
        return new Board(s);
    }

    protected boolean isSimpleAction(List<Move> moves) {
        int pos = 0;
        while (pos < moves.size() && (moves.get(pos) == ROTATE_CW || moves.get(pos) == ROTATE_CCW)) {
            pos++;
        }
        if (pos == moves.size()) {
            return false;
        }
        while (pos < moves.size() && (moves.get(pos) == LEFT || moves.get(pos) == RIGHT)) {
            pos++;
        }
        return pos == moves.size() || pos == moves.size() - 1 && moves.get(pos) == DROP;
    }

    protected void possibleGarbage(int garbage) {
        testBuilder.possibleGarbage = garbage;
    }

    protected void skipCnt(int skipCnt) {
        testBuilder.skipCnt = skipCnt;
    }

    protected void combo(int combo) {
        testBuilder.combo = combo;
    }

    protected void nextType(TetriminoType nextType) {
        testBuilder.nextTetrimino = nextType;
    }

    protected void fallingType(TetriminoType fallingType) {
        testBuilder.fallingTetriminoType = fallingType;
    }

    protected int width() {
        return testBuilder.board.getWidth();
    }

    void checkForbidden(int leftCol, int cwRotations) {
        assertFalse(testBuilder.findBestAction().equals(new Action(leftCol, cwRotations)));
    }

    protected void checkMoves(Move... moves) {
        List<Move> actual = testBuilder.findBestMoves();
        List<Move> expected = Arrays.asList(moves);
        assertEquals(actual, expected, "\nactual = " + actual + "\nexpected = " + expected + "\n");
    }

    protected void checkForbiddenMoves(Move... moves) {
        assertFalse(testBuilder.findBestMoves().equals(Arrays.asList(moves)));
    }

    void checkAction(int leftCol, int cwRotations) {
        assertEquals(testBuilder.findBestAction(), new Action(leftCol, cwRotations));
    }

    protected void board(String s) {
        testBuilder.board = newBoard(s);
    }

    class TestBuilder {
        Board board;
        TetriminoWithPosition fallingTetrimino;
        TetriminoType fallingTetriminoType;
        TetriminoType nextTetrimino;
        int combo;
        int round = 1;
        int skipCnt;
        int possibleGarbage;

        public void build() {
            if (fallingTetrimino == null) {
                if (fallingTetriminoType != null) {
                    fallingTetrimino = board.newFallingTetrimino(fallingTetriminoType);
                } else {
                    fallingTetrimino = board.extractFallingTetrimino();
                }
            }
        }

        Action findBestAction() {
            List<Move> moves = findBestMoves();
            if (!isSimpleAction(moves)) {
                throw new RuntimeException("not simple action: " + moves);
            }
            int cwRotations = 0;
            int colShift = 0;
            for (Move move : moves) {
                if (move == ROTATE_CW) {
                    cwRotations = (cwRotations + 1) % 4;
                } else if (move == ROTATE_CCW) {
                    cwRotations = (cwRotations + 3) % 4;
                } else if (move == LEFT) {
                    colShift--;
                } else if (move == RIGHT) {
                    colShift++;
                }
            }
            for (int i = 0; i < cwRotations; i++) {
                fallingTetrimino = fallingTetrimino.rotateCW();
            }
            return new Action(fallingTetrimino.getLeftCol() + colShift, cwRotations);
        }

        List<Move> findBestMoves() {
            build();
            GameState gameState = new GameState(
                    board,
                    fallingTetrimino,
                    nextTetrimino,
                    combo,
                    round,
                    skipCnt,
                    possibleGarbage
            );
            return bestMoveFinder.findBestMoves(gameState);
        }
    }
}

package tests.tetris.logic;

import org.testng.annotations.Test;
import tetris.*;
import tetris.logic.Action;
import tetris.logic.BestMoveFinder;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static tetris.Move.*;
import static tetris.TetriminoType.*;

@Test
public class BestMoveFinderTest {

    private final BestMoveFinder bestMoveFinder = new BestMoveFinder();

    @Test
    void test() {
        Board board = new Board(
                "" +
                        "..........\n" +
                        "....xxxx..\n" +
                        "..........\n" +
                        "..........\n" +
                        "x.........\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx."
        );
        checkAction(board, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testClearFull() {
        Board board = new Board(
                "" +
                        "x.........\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx."
        );
        Tetrimino tetrimino = Tetrimino.of(I).rotateCW();
        Board newBoard = board.drop(new TetriminoWithPosition(1, board.getWidth() - 1, tetrimino)).getBoard();
        Board expectedNewBoard = new Board(
                "" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "x........."
        );
        assertEquals(newBoard, expectedNewBoard);
    }

    @Test
    void testNextTetrimino() {
        Board board = new Board(
                "" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        ".......x..\n" +
                        ".......x..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n"
        );
        Tetrimino tetrimino = Tetrimino.of(I);
        Action action = findBestAction(board, tetrimino, Tetrimino.of(I));
        assertEquals(action, new Action(board.getWidth() - 2, 1));
    }

    @Test
    void minimizeLowTiles() {
        Board board = new Board(
                "" +
                        "......x....\n" +
                        "......x....\n" +
                        "......x....\n" +
                        "......x....\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx."
        );
        Tetrimino tetrimino = Tetrimino.of(I);
        Action action = findBestAction(board, tetrimino);
        assertEquals(action, new Action(1, 1));
    }

    @Test
    void testEquals() {
        assertEquals(
                new Board(
                        "" +
                                "....x.....\n" +
                                "...xxx....\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "x.........\n" +
                                "xxx......."
                ),
                new Board(
                        "" +
                                "....x.....\n" +
                                "...xxx....\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "..........\n" +
                                "x.........\n" +
                                "xxx......."
                )
        );
    }

    @Test
    void testBug() {
        Board board = new Board(
                "" +
                        "..........\n" +
                        "...xxxx...\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        ".xxxxxxxx."
        );
        checkAction(board, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testBug4() {
        Board board = new Board(
                "....x.....\n" +
                        "....x.....\n" +
                        "....x.....\n" +
                        "....x.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxx.xxx.\n" +
                        "xxxxxx.xx.\n" +
                        "xxxxxxx.x.\n" +
                        "xxxxxxxx.x\n" +
                        "xxxxxxxxx."
        );
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(board.getWidth() - 1, 0));
    }

    @Test
    void avoidBigHeight() {
        Board board = new Board(
                "" +
                        "....x.....\n" +
                        "....x.....\n" +
                        "....x.....\n" +
                        "....x.....\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        "xxxxxx....\n" +
                        "xxxxxx....\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx."
        );
        Action bestAction = getAction(board);
        Action forbiddenAction = new Action(board.getWidth() - 2, 0);
        assertFalse(bestAction.equals(forbiddenAction));
    }

    @Test
    void testBug3() {
        Board board = new Board(
                "" +
                        ".....x....\n" +
                        ".....x....\n" +
                        ".....x....\n" +
                        ".....x....\n" +
                        "x.........\n" +
                        "xxxx......\n" +
                        "xxxx...xxx\n" +
                        "xxxx...xxx\n" +
                        "xxxx..xxxx\n" +
                        "xxxx..xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "xxxxx.xxxx\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        checkAction(board, new Action(5, 0));
    }

    @Test
    void testNew() {
        Board board = new Board(
                "" +
                        "..........\n" +
                        "...xxxx...\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xx........\n" +
                        "xx........\n" +
                        "xx........\n" +
                        "xx.....x..\n" +
                        "xx..xxxxx.\n" +
                        "xx..xxxxx.\n" +
                        "xx..xxxxx.\n" +
                        "xxx.xxxxxx\n" +
                        "xxx.xxxxxx\n" +
                        "oooooooooo"
        );
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), Tetrimino.of(L));
        assertFalse(bestAction.equals(new Action(board.getWidth() - 2, 1)));
    }

    @Test
    void testBug2() {
        Board board = new Board(
                "" +
                        "...x......\n" +
                        "...xxx....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "x.........\n" +
                        "xx........\n" +
                        "xxxxxxx...\n" +
                        "xxxxxxx.x.\n" +
                        "xxxxxxx.x.\n" +
                        "xx.xxxxxxx\n" +
                        "oooooooooo"
        );
        checkForbidden(board, new Action(board.getWidth() - 3, 1));
    }

    @Test
    void testTooHigh() {
        Board board = new Board(
                "" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "xxxx......\n" +
                        "xxxx...x..\n" +
                        "xxxxx.xx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxxx.\n" +
                        "xxxx.xxxxx\n" +
                        "xxxxxx.xxx\n" +
                        "xxxxxxxx.x\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        "xxxxxxxx.x\n" +
                        ".xxxxxxxxx\n" +
                        "xxxxxx.xxx\n" +
                        "xxxx.xxxxx\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        Action action = findBestAction(board, Tetrimino.of(I));
        assertEquals(action, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testCombo() {
        Board board = new Board(
                "" +
                        "...x......\n" +
                        "...xxx....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxxxx."
        );
        Action action = findBestAction(board, board.extractFallingTetrimino(), Tetrimino.of(J), 3);
        assertEquals(action, new Action(board.getWidth() - 3, 2));
    }

    @Test
    void getDontPutOnTopBad() {
        Board board = new Board(
                "" +
                        "...xx.....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxxxx.\n" +
                        ".xxxxxxxxx"
        );
        checkForbidden(board, new Action(0, 0));
    }

    @Test
    void testComplexMove() {
        Board board = new Board(
                "" +
                        ".........\n" +
                        ".........\n" +
                        ".........\n" +
                        ".........\n" +
                        ".........\n" +
                        "....x....\n" +
                        "....x....\n" +
                        "...xx....\n" +
                        "x........\n" +
                        "........."
        );
        List<Move> bestMoves = findBestMoves(board);
        check(
                bestMoves,
                LEFT,
                LEFT,
                DOWN,
                DOWN,
                LEFT
        );
    }

    @Test
    void testIRotation() {
        TetriminoWithPosition t0 = new TetriminoWithPosition(0, 0, Tetrimino.of(I));
        TetriminoWithPosition t1 = new TetriminoWithPosition(-1, 2, Tetrimino.of(I, 1));
        TetriminoWithPosition t2 = new TetriminoWithPosition(1, 0, Tetrimino.of(I, 2));
        TetriminoWithPosition t3 = new TetriminoWithPosition(-1, 1, Tetrimino.of(I, 3));

        assertEquals(t0.rotateCW(), t1);
        assertEquals(t1.rotateCW(), t2);
        assertEquals(t2.rotateCW(), t3);
        assertEquals(t3.rotateCW(), t0);
    }

    @Test
    void testTSpin() {
        Board board = new Board(
                "" +
                        ".........\n" +
                        ".........\n" +
                        "...x.....\n" +
                        "..xx.....\n" +
                        "...x.....\n" +
                        ".........\n" +
                        "xx.......\n" +
                        "x...xxxxx\n" +
                        "xx.xxxxxx"
        );
        List<Move> bestMoves = findBestMoves(board);
        check(
                bestMoves,
                ROTATE_CW,
                ROTATE_CW,
                LEFT,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                ROTATE_CW
        );
    }

    @Test
    void testPrepareForTSpin() {
        Board board = new Board(
                "" +
                        "....x.....\n" +
                        "...xxx....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "....xxxxxx\n" +
                        "xx.xxxxxxx"
        );
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), Tetrimino.of(T));
        assertEquals(bestAction, new Action(0, 1));
    }

    @Test
    void badIsNotSoBad() {
        Board board = new Board(
                "" +
                        "...xx.....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".......xx.\n" +
                        ".x.x.x.xxx"
        );
        checkForbidden(board, new Action(board.getWidth() - 3, 0));
    }

    @Test
    void badIsNotSoBad2() {
        Board board = new Board(
                "" +
                        "...xx.....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        ".x.x.x.x.x"
        );
        checkForbidden(board, new Action(4, 0));
    }

    @Test
    void badIsNotSoBad3() {
        Board board = new Board(
                "" +
                        "...xx.....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx."
        );
        checkForbidden(board, new Action(board.getWidth() - 2, 0));
    }

    @Test
    void badIsNotSoBad4() {
        Board board = new Board(
                "" +
                        "...xx.....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        ".x.x.x.x.x"
        );
        checkAction(board, new Action(4, 0));
    }

    @Test
    void testSemiBad() {
        Board board = new Board(
                "" +
                        "....xx....\n" +
                        "...xx.....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        ".x........"
        );
        checkAction(board, new Action(2, 0));
    }

    @Test
    void testBug5() {
        Board board = new Board(
                "" +
                        ".....x....\n" +
                        "...xxx....\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "......xx..\n" +
                        ".......xx.\n" +
                        ".......xxx\n" +
                        ".xx.xxxxxx\n" +
                        ".xx.xxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        "xx.xxxxxxx\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        check(
                bestMoveFinder.findBestMoves(new GameState(board, board.extractFallingTetrimino(), Tetrimino.of(T), 0)),
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                DOWN,
                RIGHT
        );
    }

    // todo test no move

    //-------- utils

    private void check(List<Move> actualMoves, Move... expectedMoves) {
        assertEquals(actualMoves, Arrays.asList(expectedMoves), "\n" + "expected: " + Arrays.toString(expectedMoves) + "\n" + "actual: " + actualMoves + "\n");
    }


    private List<Move> findBestMoves(Board board) {
        return bestMoveFinder.findBestMoves(new GameState(board, board.extractFallingTetrimino(), null, 0));
    }

    private Action findBestAction(Board board, TetriminoWithPosition fallingTetrimino, Tetrimino nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0);
    }

    private Action findBestAction(Board board, TetriminoWithPosition tetrimino) {
        return findBestAction(board, tetrimino, null, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino) {
        return findBestAction(board, fallingTetrimino, null, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino, int combo) {
        int fallingCol = BestMoveFinder.getFallingCol(board.getWidth(), fallingTetrimino.getWidth());
        int topRow = fallingTetrimino.getType() == TetriminoType.I ? 1 : 0;
        TetriminoWithPosition tetriminoWithPosition = new TetriminoWithPosition(topRow, fallingCol, fallingTetrimino);
        return findBestAction(board, tetriminoWithPosition, nextTetrimino, combo);
    }

    private Action findBestAction(Board board, TetriminoWithPosition fallingTetrimino, Tetrimino nextTetrimino, int combo) {
        GameState gameState = new GameState(board, fallingTetrimino, nextTetrimino, combo);
        List<Move> moves = bestMoveFinder.findBestMoves(gameState);
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

    private boolean isSimpleAction(List<Move> moves) {
        int pos = 0;
        while (pos < moves.size() && moves.get(pos) == ROTATE_CW || moves.get(pos) == ROTATE_CCW) {
            pos++;
        }
        if (pos == moves.size()) {
            return false;
        }
        while (pos < moves.size() && moves.get(pos) == LEFT || moves.get(pos) == RIGHT) {
            pos++;
        }
        if (pos == moves.size()) {
            return false;
        }
        return pos == moves.size() - 1 && moves.get(pos) == DROP;
    }

    private void checkForbidden(Board board, Action forbiddenAction) {
        Action bestAction = getAction(board);
        assertFalse(bestAction.equals(forbiddenAction));
    }

    private void checkAction(Board board, Action expected) {
        Action bestAction = getAction(board);
        assertEquals(bestAction, expected);
    }

    private Action getAction(Board board) {
        return findBestAction(board, board.extractFallingTetrimino());
    }
}
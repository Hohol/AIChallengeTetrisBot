package tests.tetris.logic;

import org.testng.annotations.Test;
import tetris.*;
import tetris.logic.Action;
import tetris.logic.BestMoveFinder;

import java.util.List;

import static org.testng.Assert.*;

@Test
public class BestMoveFinderTest {

    private final BestMoveFinder bestMoveFinder = new BestMoveFinder();

    @Test
    void test() {
        Board board = new Board(
                "" +
                        "....xxxx..\n" +
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
                "x.........\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxxxxxx."
        );
        Tetrimino tetrimino = Tetrimino.I.rotateCW();
        Board newBoard = board.drop(tetrimino, board.getWidth() - 1).getBoard();
        Board expectedNewBoard = new Board(
                "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "x........."
        );
        assertEquals(newBoard, expectedNewBoard);
    }

    @Test
    void testNoTetrimino() {
        Board board = new Board(
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
                        "..x..x....\n" +
                        "x.xxxxxxx.\n" +
                        ".........."
        );
        assertNull(board.extractFallingTetrimino());
    }

    @Test
    void testNextTetrimino() {
        Board board = new Board(
                "" +
                        ".......x..\n" +
                        ".......x..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx..\n"
        );
        Tetrimino tetrimino = Tetrimino.I;
        Action action = findBestAction(board, tetrimino, Tetrimino.I);
        assertEquals(action, new Action(board.getWidth() - 2, 1));
    }

    @Test
    void minimizeLowTiles() {
        Board board = new Board(
                "......x....\n" +
                        "......x....\n" +
                        "......x....\n" +
                        "......x....\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx.\n" +
                        "x.xxxxxxxx."
        );
        Tetrimino tetrimino = Tetrimino.I;
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
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), Tetrimino.L);
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
        Action action = findBestAction(board, Tetrimino.I);
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
        Action action = findBestAction(board, board.extractFallingTetrimino(), Tetrimino.J, 1);
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

    //-------- utils

    private Action findBestAction(Board board, TetriminoWithPosition fallingTetrimino, Tetrimino nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0);
    }

    private Action findBestAction(Board board, TetriminoWithPosition tetrimino) {
        return findBestAction(board, tetrimino, null, 0);
    }

    private Action findBestAction(Board board, Tetrimino tetrimino) {
        return findBestAction(board, tetrimino, null, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino, int combo) {
        int fallingCol = getFallingCol(board.getWidth(), fallingTetrimino.getWidth());
        TetriminoWithPosition tetriminoWithPosition = new TetriminoWithPosition(-1, fallingCol, fallingTetrimino);
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
            if (move == Move.ROTATE_CW) {
                cwRotations = (cwRotations + 1) % 4;
            } else if (move == Move.ROTATE_CCW) {
                cwRotations = (cwRotations + 3) % 4;
            } else if (move == Move.LEFT) {
                colShift--;
            } else if (move == Move.RIGHT) {
                colShift++;
            }
        }
        if (cwRotations == 1) {
            if (fallingTetrimino.getTetrimino().getWidth() == 4) {
                colShift += 2;
            } else {
                colShift++;
            }
        }
        return new Action(fallingTetrimino.getLeftCol() + colShift, cwRotations);
    }

    private boolean isSimpleAction(List<Move> moves) {
        int pos = 0;
        while (pos < moves.size() && moves.get(pos) == Move.ROTATE_CW || moves.get(pos) == Move.ROTATE_CCW) {
            pos++;
        }
        if (pos == moves.size()) {
            return false;
        }
        while (pos < moves.size() && moves.get(pos) == Move.LEFT || moves.get(pos) == Move.RIGHT) {
            pos++;
        }
        if (pos == moves.size()) {
            return false;
        }
        return pos == moves.size() - 1 && moves.get(pos) == Move.DROP;
    }

    private int getFallingCol(int boardWidth, int tetriminoWidth) {
        if (tetriminoWidth == 2) {
            return boardWidth / 2 - 1;
        } else {
            return boardWidth / 2 - 2;
        }
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
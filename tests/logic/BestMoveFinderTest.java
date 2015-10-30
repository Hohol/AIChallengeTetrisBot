package logic;

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

    private final BestMoveFinder bestMoveFinder = BestMoveFinder.getBest();

    @Test
    void test() {
        Board board = board("" +
                "..........\n" +
                "....xxxx..\n" +
                "..........\n" +
                "..........\n" +
                "x.........\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testClearFull() {
        Board board = board("" +
                "x.........\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        Tetrimino tetrimino = Tetrimino.of(I).rotateCW();
        Board newBoard = board.drop(new TetriminoWithPosition(board.getHeight() - 4, board.getWidth() - 1, tetrimino), DOWN, 0, 1).getBoard();
        Board expectedNewBoard = board("" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "x.........");
        assertEquals(newBoard, expectedNewBoard);
    }

    @Test
    void testNextTetrimino() {
        Board board = board("" +
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
                "xxxxxxxx..\n");
        Tetrimino tetrimino = Tetrimino.of(I);
        Action action = findBestAction(board, tetrimino, I);
        assertEquals(action, new Action(board.getWidth() - 2, 1));
    }

    @Test
    void minimizeLowTiles() {
        Board board = board("" +
                "......x....\n" +
                "......x....\n" +
                "......x....\n" +
                "......x....\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.");
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
        String s = "" +
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
                ".xxxxxxxx.";
        Board board = board(s);
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testBug4() {
        Board board = board("....x.....\n" +
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
                "xxxxxxxxx.");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(board.getWidth() - 1, 0));
    }

    @Test
    void avoidBigHeight() {
        Board board = board("" +
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
                "xxxxxxxxx.");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        Action forbiddenAction = new Action(board.getWidth() - 2, 0);
        assertFalse(bestAction.equals(forbiddenAction));
    }

    @Test
    void testBug3() {
        Board board = board("" +
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
                "oooooooooo");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(5, 0));
    }

    @Test
    void testNew() {
        Board board = board("" +
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
                "oooooooooo");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), L);
        assertFalse(bestAction.equals(new Action(board.getWidth() - 2, 1)));
    }

    @Test
    void testTooHigh() {
        Board board = board("" +
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
                "oooooooooo");
        Action action = findBestAction(board, Tetrimino.of(I));
        assertEquals(action, new Action(board.getWidth() - 1, 1));
    }

    @Test
    void testCombo() {
        Board board = board("" +
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
                "xxxxxxxxx.");
        Action action = findBestAction(board, board.extractFallingTetrimino(), J, 3, 1);
        assertEquals(action, new Action(board.getWidth() - 3, 2));
    }

    @Test
    void getDontPutOnTopBad() {
        Board board = board("" +
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
                ".xxxxxxxxx");
        checkForbidden(board, new Action(0, 0));
    }

    @Test
    void testComplexMove() {
        Board board = board("" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                ".........\n" +
                "....x....\n" +
                "....x....\n" +
                "...xx....\n" +
                "x........\n" +
                ".........");
        List<Move> bestMoves = findBestMoves(board);
        checkMoves(
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
        Board board = board("" +
                ".........\n" +
                ".........\n" +
                "...x.....\n" +
                "..xx.....\n" +
                "...x.....\n" +
                ".........\n" +
                "xx.......\n" +
                "x...xxxxx\n" +
                "xx.xxxxxx");
        List<Move> bestMoves = findBestMoves(board);
        checkMoves(
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
        Board board = board("" +
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
                "xx.xxxxxxx");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), T);
        assertEquals(bestAction, new Action(0, 1));
    }

    @Test(enabled = false)
    void badIsNotSoBad() {
        Board board = board("" +
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
                ".x.x.x.xxx");
        checkForbidden(board, new Action(board.getWidth() - 3, 0));
    }

    @Test(enabled = false)
    void badIsNotSoBad2() {
        Board board = board("" +
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
                ".x.xxx.x.x");
        checkForbidden(board, new Action(4, 0));
    }

    @Test
    void badIsNotSoBad3() {
        Board board = board("" +
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
                "xxxxxxxxx.");
        checkForbidden(board, new Action(board.getWidth() - 2, 0));
    }

    @Test
    void testSemiBad() {
        Board board = board("" +
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
                ".x........");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(2, 0));
    }

    @Test
    void testBug5() {
        Board board = board("" +
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
                "oooooooooo");
        checkMoves(
                bestMoveFinder.findBestMoves(new GameState(board, board.extractFallingTetrimino(), T, 0, 1, 0)),
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

    @Test
    void testScore() {
        Board board = board("" +
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
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                ".xxxxxxxxx\n" +
                ".xxxxxxxxx");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertEquals(bestAction, new Action(0, 0));
    }

    @Test
    void testEndGame() {
        Board board = board("" +
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
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        Action bestAction = findBestAction(board, Tetrimino.of(O), I);
        assertEquals(bestAction, new Action(board.getWidth() - 2, 0));
    }

    @Test
    void testEndGame2() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "xxxxx.....\n" +
                "xxxxx....x\n" +
                "xxxxxx..xx\n" +
                "xxxxxx.xxx\n" +
                "xxx.xxxxxx\n" +
                "xxx.xxxxxx\n" +
                "xxxxxxxxx.\n" +
                "xxxx.xxxxx\n" +
                "xxxx.xxxxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxxx.\n" +
                "xxxxxx.xxx\n" +
                "xxxxxx.xxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        findBestAction(board, Tetrimino.of(O), Z);
    }

    @Test
    void testEndGame3() {
        Board board = board("" +
                "..........\n" +
                ".xx.......\n" +
                ".xxxxxx...\n" +
                "xxxxxxxx..\n" +
                "xxxxxxxx.x\n" +
                ".xxxxxxxxx\n" +
                "x.xxxxxxxx\n" +
                ".xxxxxxxxx\n" +
                "xxxxxx.xxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxx.x\n" +
                "xx.xxxxxxx\n" +
                "xx.xxxxxxx\n" +
                "xxxxx.xxxx\n" +
                "xx.xxxxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        Action bestAction = findBestAction(board, Tetrimino.of(O), S);
        assertEquals(bestAction, new Action(board.getWidth() - 2, 0));
    }

    @Test
    void testEndGame4() {
        Board board = board("" +
                "..........\n" +
                "xxxx..xxxx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxxxx..\n" +
                "xxxxxxxxx.\n" +
                ".xxxxxxxxx\n" +
                "x.xxxxxxxx\n" +
                ".xxxxxxxxx\n" +
                "xxxxxx.xxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxx.x\n" +
                "xx.xxxxxxx\n" +
                "xx.xxxxxxx\n" +
                "xxxxx.xxxx\n" +
                "xx.xxxxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        checkMoves(
                bestMoveFinder.findBestMoves(new GameState(board, new TetriminoWithPosition(0, 4, Tetrimino.of(O)), null, 0, 1, 0))
        );
    }

    @Test
    void testEndGame5() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
                "xx.x.xx...\n" +
                "xx.xxxx...\n" +
                "xx.xxxx.xx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxxx.\n" +
                "xxx.xxxxxx\n" +
                "xxxxxx.xxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxx.x\n" +
                "xxxx.xxxxx\n" +
                "xxxx.xxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        Action bestAction = findBestAction(board, Tetrimino.of(S), S);
        assertEquals(bestAction, new Action(board.getWidth() - 3, 0));
    }

    @Test
    void testEndGame6() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
                "....xxxxx.\n" +
                "xxxxxxx...\n" +
                "xx.xxxx.xx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxxx.\n" +
                "xxx.xxxxxx\n" +
                "xxxxxx.xxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxx.x\n" +
                "xxxx.xxxxx\n" +
                "xxxx.xxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        Action bestAction = findBestAction(board, Tetrimino.of(I), O);
        assertEquals(bestAction, new Action(board.getWidth() - 4, 0));
    }

    @Test
    void considerSolidBlocks() {
        Board board = board("" +
                "..........\n" +
                "...xxxx...\n" +
                "..........\n" +
                ".xxxx..xxx\n" +
                "xxxxxx.xxx\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxx.x\n" +
                "xxxxxxxx.x\n" +
                "xxxxxx.xxx\n" +
                "xxxxxxxxx.\n" +
                "xxx.xxxxxx\n" +
                "xxxxxx.xxx\n" +
                "x.xxxxxxxx\n" +
                "xxxxxxxx.x\n" +
                "xxxx.xxxxx\n" +
                "xxxx.xxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        Action bestAction = findBestAction(board, board.extractFallingTetrimino(), I, 0, 20);
        assertEquals(bestAction, new Action(board.getWidth() - 4, 1));
    }

    @Test
    void testSkip() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
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
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        checkMoves(
                bestMoveFinder.findBestMoves(new GameState(board, board.newFallingTetrimino(O), I, 0, 1, 1)),
                SKIP
        );
    }

    @Test
    void dontWasteSkip() {
        Board board = board("" +
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
                "..........");
        List<Move> actualMoves = bestMoveFinder.findBestMoves(new GameState(board, board.newFallingTetrimino(S), null, 0, 1, 1));
        assertFalse(actualMoves.size() == 1 && actualMoves.get(0) == SKIP);
    }

    @Test
    void testBug2() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                ".x........\n" +
                "xx........\n" +
                ".x........\n" +
                "..........\n" +
                "..........\n" +
                "..xxx.....\n" +
                "...xx...xx\n" +
                "x.xxxxxxxx\n" +
                "xxxxx..xxx\n" +
                ".xxxxxxxxx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        List<Move> actualMoves = bestMoveFinder.findBestMoves(new GameState(board, board.extractFallingTetrimino(), T, 0, 1, 0));
        assertFalse(actualMoves.equals(Arrays.asList(DOWN, DOWN, DOWN, DOWN, DOWN, ROTATE_CCW)));
    }

    @Test
    void testBug6() {
        Board board = board("" +
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
                "xxxxxx..xx\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo\n" +
                "oooooooooo");
        bestMoveFinder.findBestMoves(new GameState(board, board.newFallingTetrimino(O), Z, 0, 1, 1));
    }

    @Test
    void useSkip() {
        Board board = board("" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "x.x.x.x.x.\n" +
                "x.x.x.x.x.\n" +
                "x.x.x.x.x.\n" +
                "x.x.x.x.x.\n" +
                "x.x.x.x.x."
        );

        checkMoves(
                bestMoveFinder.findBestMoves(new GameState(board, board.newFallingTetrimino(O), I, 0, 1, 1)),
                SKIP
        );
    }

    //-------- utils

    private void checkMoves(List<Move> actualMoves, Move... expectedMoves) {
        assertEquals(actualMoves, Arrays.asList(expectedMoves), "\n" + "expected: " + Arrays.toString(expectedMoves) + "\n" + "actual: " + actualMoves + "\n");
    }


    private List<Move> findBestMoves(Board board) {
        return bestMoveFinder.findBestMoves(new GameState(board, board.extractFallingTetrimino(), null, 0, 1, 0));
    }

    private Action findBestAction(Board board, TetriminoWithPosition fallingTetrimino, TetriminoType nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0, 1);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, TetriminoType nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0);
    }

    private Action findBestAction(Board board, TetriminoWithPosition tetrimino) {
        return findBestAction(board, tetrimino, null, 0, 1);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino) {
        return findBestAction(board, fallingTetrimino, null, 0);
    }

    private Action findBestAction(Board board, Tetrimino fallingTetrimino, TetriminoType nextTetrimino, int combo) {
        TetriminoWithPosition tetriminoWithPosition = board.newFallingTetrimino(fallingTetrimino.getType());
        return findBestAction(board, tetriminoWithPosition, nextTetrimino, combo, 1);
    }

    private Action findBestAction(Board board, TetriminoWithPosition fallingTetrimino, TetriminoType nextTetrimino, int combo, int round) {
        GameState gameState = new GameState(board, fallingTetrimino, nextTetrimino, combo, round, 0);
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

    private void checkForbidden(Board board, Action forbiddenAction) {
        Action bestAction = findBestAction(board, board.extractFallingTetrimino());
        assertFalse(bestAction.equals(forbiddenAction));
    }

    public static Board board(String s) {
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
}
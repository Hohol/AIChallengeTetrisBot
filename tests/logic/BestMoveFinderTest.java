package logic;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tetris.*;
import tetris.logic.Action;
import tetris.logic.BestMoveFinder;
import tetris.logic.EvaluationParameter;
import tetris.logic.ParameterWeights;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static tetris.Move.*;
import static tetris.TetriminoType.*;
import static tetris.logic.EvaluationParameter.*;

@Test
public class BestMoveFinderTest {

    public static final ParameterWeights HEIGHT_ONLY = ParameterWeights.zero().put(HEIGHT, 1).put(HEIGHT_POW, 1);
    private BestMoveFinder bestMoveFinder;

    TestBuilder testBuilder;

    @BeforeMethod
    void init() {
        bestMoveFinder = BestMoveFinder.getBest();
        testBuilder = new TestBuilder();
    }

    @Test
    void test() {
        board("" +
                "..........\n" +
                "....xxxx..\n" +
                "..........\n" +
                "..........\n" +
                "x.........\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        checkAction(width() - 1, 1);
    }

    @Test
    void testClearFull() {
        Board board = newBoard("" +
                "x.........\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.\n" +
                "xxxxxxxxx.");
        Tetrimino tetrimino = Tetrimino.of(I).rotateCW();
        Board newBoard = board.drop(new TetriminoWithPosition(board.getHeight() - 4, board.getWidth() - 1, tetrimino), DOWN, 0, 1).getBoard();
        Board expectedNewBoard = newBoard("" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "..........\n" +
                "x.........");
        assertEquals(newBoard, expectedNewBoard);
    }

    @Test
    void testNextTetrimino() {
        board("" +
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
        fallingType(I);
        nextType(I);
        checkAction(width() - 2, 1);
    }

    @Test
    void minimizeLowTiles() {
        board("" +
                "......x....\n" +
                "......x....\n" +
                "......x....\n" +
                "......x....\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.\n" +
                "x.xxxxxxxx.");
        fallingType(I);
        checkAction(1, 1);
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
        board("" +
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
                ".xxxxxxxx.");
        checkAction(width() - 1, 1);
    }

    @Test
    void testBug4() {
        board("" +
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
                "xxxxxxxxx.");
        checkAction(width() - 1, 0);
    }

    @Test
    void avoidBigHeight() {
        board("" +
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
        checkForbidden(width() - 2, 0);
    }

    @Test
    void testBug3() {
        board("" +
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
        checkAction(5, 0);
    }

    @Test
    void testNew() {
        board("" +
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
        nextType(L);
        checkForbidden(width() - 2, 1);
    }

    @Test
    void testCombo() {
        board("" +
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
        nextType(J);
        combo(3);
        checkAction(width() - 3, 2);
    }

    @Test(enabled = false)
        // todo rework to defense only
    void getDontPutOnTopBad() {
        board("" +
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
        checkForbidden(0, 0);
    }

    @Test
    void testComplexMove() {
        board("" +
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
        checkMoves(
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
        board("" +
                ".........\n" +
                ".........\n" +
                "...x.....\n" +
                "..xx.....\n" +
                "...x.....\n" +
                ".........\n" +
                "xx.......\n" +
                "x...xxxxx\n" +
                "xx.xxxxxx");
        checkMoves(
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
        board("" +
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
        nextType(T);
        checkAction(0, 1);
    }

    @Test
    void badIsNotSoBad3() {
        board("" +
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
        checkForbidden(width() - 2, 0);
    }

    @Test
    void testSemiBad() {
        board("" +
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
        checkAction(2, 0);
    }

    @Test
    void testScore() {
        board("" +
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
        checkAction(0, 0);
    }

    @Test
    void testEndGame() {
        board("" +
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
        fallingType(O);
        nextType(I);
        checkAction(width() - 2, 0);
    }

    @Test
    void testEndGame2() {
        board("" +
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
        fallingType(O);
        nextType(Z);
        testBuilder.findBestAction(); // just no exceptions
    }

    @Test
    void testEndGame3() {
        board("" +
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
        fallingType(O);
        nextType(Z);
        checkAction(width() - 2, 0);
    }

    @Test
    void testEndGame4() {
        board("" +
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
        testBuilder.fallingTetrimino = new TetriminoWithPosition(0, 4, O);
        checkMoves();
    }

    @Test
    void testEndGame6() {
        board("" +
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
        fallingType(I);
        nextType(O);
        checkAction(width() - 4, 0);
    }

    @Test
    void considerSolidBlocks() {
        board("" +
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
        nextType(I);
        testBuilder.round = Board.SOLID_BLOCK_PERIOD;
        checkAction(width() - 4, 1);
    }

    @Test
    void testSkip() {
        board("" +
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
        fallingType(O);
        nextType(I);
        skipCnt(1);
        checkMoves(
                SKIP
        );
    }

    @Test(enabled = false)
        // todo rework to more obvious
    void dontWasteSkip() {
        board("" +
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
        fallingType(S);
        skipCnt(1);
        List<Move> actualMoves = testBuilder.findBestMoves();
        assertFalse(actualMoves.size() == 1 && actualMoves.get(0) == SKIP);
    }

    @Test
    void testBug6() {
        board("" +
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
        fallingType(O);
        nextType(Z);
        skipCnt(1);
        testBuilder.findBestMoves(); // just no exception
    }

    @Test
    void useSkip() {
        board("" +
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
        fallingType(O);
        nextType(I);
        skipCnt(1);
        checkMoves(
                SKIP
        );
    }

    @Test
    void tSpinPattern() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "...xxxxxxx\n" +
                        "x.xxxxxxxx"
        );
        fallingType(I);
        checkAction(2, 0);
    }

    @Test
    void semiTSpinPattern() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "...xxxxxx.\n" +
                        "x.xxxxxxxx"
        );
        fallingType(T);
        checkAction(width() - 2, 3);
    }

    @Test
    void avoidLowEfficiency() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxx.......\n" +
                        "x.........\n" +
                        "..........\n" +
                        ".xxxxxxxxx"
        );
        checkForbidden(0, 0);
    }

    @Test
    void testLongHole() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        ".xx.......\n" +
                        ".xx.......\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "...xxxxxxx\n" +
                        "...xxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxx."
        );
        checkForbidden(1, 0);
    }

    @Test
    void cellsAboveTopBadBug() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        ".xxxxxxxx.\n" +
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
                        ".xxxxxxxxx"
        );
        fallingType(O);
        checkForbidden(0, 0);
    }

    @Test
    void testAboveBad() {
        board("" +
                        "......x...\n" +
                        ".....xx...\n" +
                        ".....x....\n" +
                        "xxxx......\n" +
                        "xxxxxxxx.x\n" +
                        "xxxxxxxxx.\n" +
                        "xxxxx.xxxx\n" +
                        "xxxx..xxxx\n" +
                        "xxxx.xxxxx\n" +
                        "xxx..xxxxx\n" +
                        ".xxxxxxxxx\n" +
                        "xx.xxx.xxx\n" +
                        "xx.xxxxxxx\n" +
                        ".xxxx.xxxx\n" +
                        "xxxx.xxxxx\n" +
                        "xxx.xxxxx.\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        nextType(O);
        checkForbidden(width() - 2, 0);
    }

    @Test
    void testEndGame7() {
        board("" +
                "..........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x.........\n" +
                "x..xxxxxx.\n" +
                "x..xxxxxx.");
        fallingType(O);
        testBuilder.round = 15;
        checkAction(1, 0);
    }

    @Test
    void watchOpponent() {
        board("" +
                        "..........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "x......x..\n" +
                        "xxxxxxx...\n" +
                        "xxxxxxxx.x"
        );
        fallingType(I);
        nextType(T);
        possibleGarbage(1);
        checkAction(width() - 2, 1);
    }

    @Test
    void watchOpponent2() {
        board("" +
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
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "....xx....\n" +
                        "xxxxxxxx..\n" +
                        "xxxxxxxx.."
        );
        fallingType(O);
        possibleGarbage(1);
        checkAction(width() - 2, 0);
    }

    @Test
    void watchOpponent3() {
        board("" +
                        "..........\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        "..x.xxxxxx\n" +
                        "..x.xxxxxx"
        );
        fallingType(O);
        possibleGarbage(1);
        checkAction(0, 0);
    }

    @Test
    void watchOpponent4() {
        board("" +
                        "..........\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".........x\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx"
        );
        fallingType(Z);
        nextType(I);
        skipCnt(1);
        possibleGarbage(1);
        checkAction(0, 1);
    }

    @Test
    void endGameBug() {
        board("" +
                        "..........\n" +
                        "xx........\n" +
                        "xxxxx....x\n" +
                        "xxxxxxx.xx\n" +
                        ".xxxxxxxxx\n" +
                        "x.xxxxxxxx\n" +
                        "xxx.xxxx.x\n" +
                        ".xxxxxxxxx\n" +
                        ".x.xxxxxxx\n" +
                        "xxxxxx.xxx\n" +
                        "x.xxxx.xxx\n" +
                        "xxxxxx.xxx\n" +
                        "xxxx.xxx.x\n" +
                        "xxxxxxxx.x\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        fallingType(O);
        nextType(S);
        checkForbidden(2, 0);
    }

    @Test
    void testEfficiencyBug() {
        board("" +
                        "..........\n" +
                        "...xxx....\n" +
                        ".....x....\n" +
                        "..........\n" +
                        "xxxxxxxxx."
        );
        nextType(O);
        checkForbidden(width() - 3, 0);
    }

    @Test
    void endGameBug3() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxx.xx\n" +
                        "xxxxxxx.xx\n" +
                        ".xxxxxxxxx\n" +
                        "x.xxxxxxxx\n" +
                        "xxx.xxxx.x\n" +
                        ".xxxxxxxxx\n" +
                        ".x.xxxxxxx\n" +
                        "xxxxxx.xxx\n" +
                        "x.xxxx.xxx\n" +
                        "xxxxxx.xxx\n" +
                        "xxxx.xxx.x\n" +
                        "xxxxxxxx.x\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        fallingType(I);
        checkForbiddenMoves(
                RIGHT, RIGHT, ROTATE_CW
        );
    }

    @Test
    void testIPattern() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "x...xxxxxx\n" +
                        "x...xxxxxx\n" +
                        "xxx.xxxxxx\n" +
                        "xxx.xxxxxx"
        );
        fallingType(O);
        checkAction(1, 0);
    }


    @Test
    void endGameBug4() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "......xx..\n" +
                        "x...xxxxx.\n" +
                        "xx.xxxxxxx\n" +
                        "xx.xxxxxxx\n" +
                        "xx.xxxxxxx\n" +
                        "xx.xxxxxxx\n" +
                        "xx.xxxxxxx\n" +
                        "xx.xx.xxxx\n" +
                        "x.xxxxxxxx\n" +
                        "xxx.xx.xxx\n" +
                        "xxxx.xxxxx\n" +
                        "xxxxx.xxx.\n" +
                        "xxx.xxxxxx\n" +
                        "xxxxx.xx.x\n" +
                        "xxxx.xxxxx\n" +
                        "x.xxxxx.xx\n" +
                        "oooooooooo\n" +
                        "oooooooooo\n" +
                        "oooooooooo"
        );
        testBuilder.round = 14;
        fallingType(O);
        nextType(Z);
        checkForbidden(4, 0);
    }

    @Test
    void testOneMoreLevel() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "xxxxxxx...\n" +
                        "xxxxxxx..."
        );
        bestMoveFinder = new BestMoveFinder(HEIGHT_ONLY, 2);
        fallingType(O);
        checkAction(width() - 2, 0);
    }

    @Test
    void testOneMoreLevel2() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x........\n" +
                        ".x..xxxx..\n" +
                        ".x..xxxx..\n" +
                        ".x..xxxxxx\n" +
                        ".xx.xxxxxx"
        );
        bestMoveFinder = new BestMoveFinder(HEIGHT_ONLY, 2);
        fallingType(O);
        nextType(T);
        checkAction(width() - 2, 0);
    }

    @Test
    void testOneMoreLevel3() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "........x.\n" +
                        "........x.\n" +
                        "........x.\n" +
                        "..xxxxxxxx\n" +
                        ".xxxxxxxxx\n" +
                        ".xxxxxxxxx"
        );
        bestMoveFinder = new BestMoveFinder(ParameterWeights.zero().put(SCORE, -1), 2);
        fallingType(O);
        checkForbidden(0, 0);
    }

    @Test
    void testOneMoreLevel4() {
        board("" +
                        "..........\n" +
                        "..........\n" +
                        "xxx.....xx\n" +
                        "xxxx.xxxxx"
        );
        bestMoveFinder = new BestMoveFinder(ParameterWeights.zero().put(SCORE, -1), 4);
        fallingType(O);
        nextType(O);
        checkAction(6, 0);
    }

    //-------- utils

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

        private List<Move> findBestMoves() {
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

    private void board(String s) {
        testBuilder.board = newBoard(s);
    }

    void checkAction(int leftCol, int cwRotations) {
        assertEquals(testBuilder.findBestAction(), new Action(leftCol, cwRotations));
    }

    private void checkMoves(Move... moves) {
        List<Move> actual = testBuilder.findBestMoves();
        List<Move> expected = Arrays.asList(moves);
        assertEquals(actual, expected, "\nactual = " + actual + "\nexpected = " + expected + "\n");
    }

    private void checkForbiddenMoves(Move... moves) {
        assertFalse(testBuilder.findBestMoves().equals(Arrays.asList(moves)));
    }

    void checkForbidden(int leftCol, int cwRotations) {
        assertFalse(testBuilder.findBestAction().equals(new Action(leftCol, cwRotations)));
    }

    private int width() {
        return testBuilder.board.getWidth();
    }

    private void fallingType(TetriminoType fallingType) {
        testBuilder.fallingTetriminoType = fallingType;
    }

    private void nextType(TetriminoType nextType) {
        testBuilder.nextTetrimino = nextType;
    }

    private void combo(int combo) {
        testBuilder.combo = combo;
    }

    private void skipCnt(int skipCnt) {
        testBuilder.skipCnt = skipCnt;
    }

    private void possibleGarbage(int garbage) {
        testBuilder.possibleGarbage = garbage;
    }
}
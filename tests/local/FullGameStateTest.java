package local;

import org.testng.annotations.Test;
import tetris.Board;
import tetris.Holes;
import tetris.Move;

import static tetris.Move.*;
import static tetris.TetriminoType.*;

import tetris.TetriminoType;
import tetris.logic.BestMoveFinder;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@Test
public class FullGameStateTest {
    @Test
    void testSimple() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "...."
        ));
        state.combo = 1;
        makeMove(state, L, bmf(DOWN, DOWN));
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "..x.\n" +
                                "xxx."
                ));
    }

    @Test
    void testClear() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "...x"
        ));
        state.combo = 2;
        state.score = 3;
        makeMove(state, L, bmf(DOWN, DOWN));
        check(
                state,
                5, 2, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "..x."
                ));
    }

    @Test
    void testTSpin() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "..x.\n" +
                        "....\n" +
                        "x.xx"
        ));
        makeMove(state, T, bmf(DOWN, ROTATE_CCW, DOWN, DOWN, ROTATE_CCW));
        check(
                state,
                5, 1, false, 1, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "..x.\n" +
                                "xxx."
                ));
    }

    @Test
    void testDoubleTSpin() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "..x.\n" +
                        "...x\n" +
                        "x.xx"
        ));
        makeMove(state, T, bmf(DOWN, ROTATE_CCW, DOWN, DOWN, ROTATE_CCW));
        check(
                state,
                10, 1, false, 3, 1,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "..x."
                ));
    }

    @Test
    void testPerfectClear() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "..xx\n" +
                        "..xx"
        ));
        makeMove(state, O, bmf(DOWN, DOWN, DOWN, DOWN));
        check(
                state,
                18, 1, false, 6, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "...."
                ));
    }

    @Test
    void testHang() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "...x\n" +
                        "x..."
        ));
        makeMove(state, L, bmf());
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "..x.\n" +
                                "x..."
                ));
    }

    @Test
    void testSolidBlock() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "x..."
        ));
        state.round = 15;
        makeMove(state, L, bmf());
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "..x.\n" +
                                "xxx.\n" +
                                "x...\n" +
                                "oooo"
                ));
    }

    @Test
    void testAddGarbage() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "x..."
        ));
        state.addGarbage(new Holes(0));
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "x...\n" +
                                ".xxx"
                ));
    }

    @Test
    void testAddNoGarbage() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "x..."
        ));
        state.addGarbage();
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "x..."
                ));
    }

    @Test
    void testGarbageWithSolid() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "..x.\n" +
                        ".x..\n" +
                        "oooo\n" +
                        "oooo"
        ));
        state.addGarbage(new Holes(2), new Holes(3));
        check(
                state,
                0, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "..x.\n" +
                                ".x..\n" +
                                "xx.x\n" +
                                "xxx.\n" +
                                "oooo\n" +
                                "oooo"
                ));
    }

    @Test
    void losingMove() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "x...\n" +
                        "x..."
        ));
        makeMove(state, O, bmf());
        check(
                state,
                0, 0, true, 0, 0,
                new Board(
                        "" +
                                "xx..\n" +
                                "xx..\n" +
                                "x...\n" +
                                "x..."
                ));
    }

    @Test
    void loseAfterSolidBlock() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "...x\n" +
                        "...x\n" +
                        "...x"
        ));
        state.round = 30;
        makeMove(state, O, bmf());
        check(
                state,
                0, 0, true, 0, 0,
                new Board(
                        "" +
                                "...x\n" +
                                "xx.x\n" +
                                "xx.x\n" +
                                "oooo"
                ));
    }

    @Test
    void loseAfterGarbage() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "...x\n" +
                        "...x\n" +
                        "...x"
        ));
        state.addGarbage(new Holes(0));
        check(
                state,
                0, 0, true, 0, 0,
                new Board(
                        "" +
                                "...x\n" +
                                "...x\n" +
                                "...x\n" +
                                ".xxx"
                ));
    }

    @Test
    void endGame2() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "..xx\n" +
                        "xxx.\n" +
                        "xxx."
        ));
        makeMove(state, O, bmf());
        check(
                state,
                0, 0, true, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "xx..\n" +
                                "xxx.\n" +
                                "xxx."
                ));
    }

    @Test
    void cantSpawn() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "x.xx\n" +
                        "xxx.\n" +
                        "xxx."
        ));
        makeMove(state, O, null);
        check(
                state,
                0, 0, true, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "x.xx\n" +
                                "xxx.\n" +
                                "xxx."
                ));
    }

    @Test
    void testBug() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "...."
        ));
        state.score = 20;
        makeMove(state, O, bmf());
        check(
                state,
                20, 0, false, 0, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "xx..\n" +
                                "xx.."
                ));
    }

    @Test
    void perfectClearWithSolidBlocks() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "..xx\n" +
                        "..xx\n" +
                        "oooo"
        ));
        makeMove(state, O, bmf());
        check(
                state,
                18, 1, false, 6, 0,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "oooo"
                ));
    }

    @Test
    void testSkip() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "..xx\n" +
                        "..xx\n" +
                        "oooo"
        ));
        state.combo = 3;
        state.score = 1;
        state.skipCnt = 2;
        makeMove(state, O, bmf(SKIP));
        check(
                state,
                1, 3, false, 0, 1,
                new Board(
                        "" +
                                "....\n" +
                                "..xx\n" +
                                "..xx\n" +
                                "oooo"
                ));
    }

    @Test
    void getNewSkip() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "..xx\n" +
                        "...x\n" +
                        "x.xx"
        ));
        makeMove(state, T, bmf(ROTATE_CCW, DOWN, DOWN, ROTATE_CCW));
        check(
                state,
                10, 1, false, 3, 1,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "..xx"
                ));
    }

    @Test
    void getNewSkip2() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "....\n" +
                        "...x\n" +
                        ".xxx\n" +
                        ".xxx\n" +
                        ".xxx\n" +
                        ".xxx"
        ));
        makeMove(state, I, bmf(DOWN, ROTATE_CW, LEFT, LEFT, DROP));
        check(
                state,
                10, 1, false, 3, 1,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "...x"
                ));
    }

    @Test
    void endGame() {
        FullGameState state = new FullGameState(new Board("" +
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
        ));
        makeMove(state, I, bmf(RIGHT, RIGHT, ROTATE_CW));
        check(
                state, 3, 1, true, 1, 0, new Board("" +
                        "..........\n" +
                        "..........\n" +
                        ".......x..\n" +
                        ".......x..\n" +
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
                )
        );
    }

    // --------------- utils

    private void makeMove(FullGameState state, TetriminoType curTetrimino, BestMoveFinder bmf) {
        state.makeMove(
                curTetrimino,
                null,
                Collections.singletonList(0), bmf
        );
    }

    private void check(
            FullGameState state,
            int score,
            int combo,
            boolean lost,
            int garbageSentOnLastMove,
            int skipCnt,
            Board board
    ) {
        assertEquals(state.board, board, "board: \n" + state.board);
        assertEquals(state.score, score, "score");
        assertEquals(state.combo, combo, "combo");
        assertEquals(state.lost, lost, "lost");
        assertEquals(state.skipCnt, skipCnt, "skipCnt");
        assertEquals(state.garbageSentOnLastMove, garbageSentOnLastMove, "garbage");
    }

    private BestMoveFinder bmf(Move... moves) {
        BestMoveFinder r = mock(BestMoveFinder.class);
        when(r.findBestMoves(any())).thenReturn(Arrays.asList(moves));
        return r;
    }
}
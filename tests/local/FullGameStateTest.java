package local;

import org.testng.annotations.Test;
import tetris.Board;
import tetris.Move;

import static tetris.Move.*;
import static tetris.TetriminoType.*;

import tetris.logic.BestMoveFinder;

import java.util.Arrays;

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
        state.makeMove(
                L,
                null,
                bmf(DOWN, DOWN)
        );
        check(
                state,
                0, 0, false, 0,
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
        state.makeMove(
                L,
                null,
                bmf(DOWN, DOWN)
        );
        check(
                state,
                6, 3, false, 1,
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
        state.makeMove(
                T,
                null,
                bmf(DOWN, ROTATE_CCW, DOWN, DOWN, ROTATE_CCW)
        );
        check(
                state,
                6, 1, false, 1,
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
        state.makeMove(
                T,
                null,
                bmf(DOWN, ROTATE_CCW, DOWN, DOWN, ROTATE_CCW)
        );
        check(
                state,
                12, 1, false, 3,
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
        state.makeMove(
                O,
                null,
                bmf(DOWN, DOWN, DOWN, DOWN)
        );
        check(
                state,
                24, 1, false, 6,
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
        state.makeMove(
                L,
                null,
                bmf()
        );
        check(
                state,
                1, 1, false, 0,
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
        state.round = 20;
        state.makeMove(
                L,
                null,
                bmf()
        );
        check(
                state,
                0, 0, false, 0,
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
        state.addGarbage(0);
        check(
                state,
                0, 0, false, 0,
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
                0, 0, false, 0,
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
        state.addGarbage(2, 3);
        check(
                state,
                0, 0, false, 0,
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
        state.makeMove(
                O,
                null,
                bmf()
        );
        check(
                state,
                0, 0, true, 0,
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
        state.round = 40;
        state.makeMove(
                O,
                null,
                bmf()
        );
        check(
                state,
                0, 0, true, 0,
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
        state.addGarbage(0);
        check(
                state,
                0, 0, true, 0,
                new Board(
                        "" +
                                "...x\n" +
                                "...x\n" +
                                "...x\n" +
                                ".xxx"
                ));
    }

    @Test
    void almostLost() {
        FullGameState state = new FullGameState(new Board(
                "" +
                        "....\n" +
                        "..xx\n" +
                        "xxx.\n" +
                        "xxx."
        ));
        state.makeMove(
                O,
                null,
                bmf()
        );
        check(
                state,
                1, 1, false, 0,
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
        state.makeMove(
                O,
                null,
                null
        );
        check(
                state,
                0, 0, true, 0,
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
        state.makeMove(
                O,
                null,
                bmf()
        );
        check(
                state,
                20, 0, false, 0,
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
        state.makeMove(
                O,
                null,
                bmf()
        );
        check(
                state,
                24, 1, false, 6,
                new Board(
                        "" +
                                "....\n" +
                                "....\n" +
                                "....\n" +
                                "oooo"
                ));
    }

    // --------------- utils

    private void check(
            FullGameState state,
            int score,
            int combo,
            boolean lost,
            int garbageSentOnLastMove,
            Board board
    ) {
        assertEquals(state.board, board, "board: \n" + state.board);
        assertEquals(state.score, score, "score");
        assertEquals(state.combo, combo, "combo");
        assertEquals(state.lost, lost, "lost");
        assertEquals(state.garbageSentOnLastMove, garbageSentOnLastMove, "garbage");
    }

    private BestMoveFinder bmf(Move... moves) {
        BestMoveFinder r = mock(BestMoveFinder.class);
        when(r.findBestMoves(any())).thenReturn(Arrays.asList(moves));
        return r;
    }
}
package logic;

import org.testng.annotations.Test;
import tetris.Move;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static tetris.Move.SKIP;
import static tetris.TetriminoType.S;

@Test
public class DisabledBMFTests extends AbstractBMFTest {
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
}

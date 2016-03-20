package logic;

import org.testng.Assert;
import org.testng.annotations.Test;

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

    @Test
    void testBug() {
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
                "xxxx..xxxx\n" +
                "xxxx..xxxx\n" +
                "xxxxx.xxxx\n" +
                "xxxxx.xxxx");
        checkForbidden(5, 0);
    }
}

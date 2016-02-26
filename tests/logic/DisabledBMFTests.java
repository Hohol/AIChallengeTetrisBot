package logic;

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
}

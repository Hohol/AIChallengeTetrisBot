package logic;

import org.testng.annotations.Test;
import tetris.Board;
import tetris.logic.EvaluationState;
import tetris.logic.Evaluator;
import tetris.logic.ParameterWeights;

import static org.testng.Assert.assertEquals;

@Test
public class EvaluatorTest {
    Evaluator evaluator = new Evaluator(new ParameterWeights().zeroOut());

    @Test
    void testTSpinPattern() {
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "..x.......\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                true);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "..x.......\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                true);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "..x.......\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxx."
                ),
                false);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x.x.......\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                false);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x.........\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                true);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x.........\n" +
                                "...xxxxxxx\n" +
                                "xxxxxxxxxx"
                ),
                false);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x.........\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "xxxxxxxxxx"
                ),
                true);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x.........\n" +
                                "x.........\n" +
                                "x.........\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "x.xxxxxxxx\n" +
                                "xxxxxxxxxx"
                ),
                true);
        checkTSpinPattern(
                board("" +
                                "..........\n" +
                                "x......x..\n" +
                                "xxxxxxx...\n" +
                                "xxxxxxxx.x\n" +
                                "xxxxxxxxxx"
                ),
                true);
    }

    @Test
    void semiTSpinPattern() {
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "..........\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                true);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "...x......\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxxx"
                ),
                false);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "......x...\n" +
                                "xxxxxxx...\n" +
                                "xxxxxxxx.x"
                ),
                false);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "..........\n" +
                                "...xxxxxxx\n" +
                                "x.xxxxxxx."
                ),
                false);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "..........\n" +
                                "....xxxxx.\n" +
                                "....xxxxx.\n" +
                                "....xxxxx.\n" +
                                "....xxxxx.\n" +
                                "...xxxxxx.\n" +
                                "x.xxxxxxx."
                ),
                false);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "..........\n" +
                                "....xxxxx.\n" +
                                "...xxxxxx.\n" +
                                "x.xxxxxxx."
                ),
                false);
        checkSemiTSpinPattern(
                board("" +
                                "..........\n" +
                                "..........\n" +
                                "...xxxxxx.\n" +
                                "x.xxxxxxx."
                ),
                true);
    }

    @Test
    void TSpinPatternAffectsOtherMetrics() {
        Board board = board("" +
                        "..........\n" +
                        "..x.......\n" +
                        "...xxxxxxx\n" +
                        "x.xxxxxxxx"
        );
        EvaluationState evaluation = getEvaluation(board);
        assertEquals(evaluation.badCnt, 0);

        board = board("" +
                        "..........\n" +
                        "x.........\n" +
                        "...xxxxxxx\n" +
                        "x.xxxxxxxx"
        );
        evaluation = getEvaluation(board);
        assertEquals(evaluation.badCnt, 0);
    }

    @Test
    void aboveBadFactor() {
        Board board = board("" +
                        "..........\n" +
                        ".........x\n" +
                        "........xx\n" +
                        "........x.\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "xxxxxx.xxx\n" +
                        "xxxxxxx.xx\n" +
                        "xxxxxxx.xx"
        );
        EvaluationState evaluation = getEvaluation(board);
        assertEquals(evaluation.aboveBadFactor, 4);
    }

    @Test
    void aboveBadFactor2() {
        Board board = board("" +
                        "..........\n" +
                        ".........x\n" +
                        "........xx\n" +
                        "........x.\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "........xx\n" +
                        "xxxxxxxx.x\n" +
                        "xxxxxxx.xx\n" +
                        "xxxxxxx.xx"
        );
        EvaluationState evaluation = getEvaluation(board);
        assertEquals(evaluation.aboveBadFactor, 2 + 9 + 10 + 10);
    }

    @Test
    void aboveBadFactor3() {
        Board board = board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "x.........\n" +
                        "x.........\n" +
                        "xxx.......\n" +
                        ".xxxxxxxxx"
        );
        EvaluationState evaluation = getEvaluation(board);
        assertEquals(evaluation.aboveBadFactor, 3);
    }

    //--------- utils

    private void checkSemiTSpinPattern(Board board, boolean expected) {
        assertEquals(getEvaluation(board).semiTSpinPattern, expected);
    }

    private void checkTSpinPattern(Board board, boolean expected) {
        assertEquals(getEvaluation(board).tSpinPattern, expected);
    }

    private EvaluationState getEvaluation(Board board) {
        return evaluator.getEvaluation(board, 0, 0, 0, 0, 0, false, 0);
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

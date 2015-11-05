package tetris.logic;

import org.testng.annotations.Test;
import tetris.Board;
import tetris.TetriminoType;

import static org.testng.Assert.*;

@Test
public class PossibleGarbageCalculatorTest {
    @Test
    void test() {
        PossibleGarbageCalculator possibleGarbageCalculator = new PossibleGarbageCalculator();
        Board board = board("" +
                        "..........\n" +
                        "..x.......\n" +
                        "...xxxxxxx\n" +
                        "x.xxxxxxxx"
        );
        assertEquals(4, possibleGarbageCalculator.calculatePossibleGarbage(board, TetriminoType.T, 2, 0));
    }

    @Test
    void test2() {
        PossibleGarbageCalculator possibleGarbageCalculator = new PossibleGarbageCalculator();
        Board board = board("" +
                        "..........\n" +
                        "..........\n" +
                        "..........\n" +
                        "...xxxxxxx\n" +
                        "x.xxxxxxxx"
        );
        assertEquals(
                1,
                possibleGarbageCalculator.calculatePossibleGarbage(board, TetriminoType.L, 2, 1)
        );
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
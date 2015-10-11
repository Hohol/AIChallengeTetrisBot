package tetris.logic;

import tetris.Board;

public class EvaluationState {
    public static final double HEIGHT_RATIO_Q = 2.3;

    private final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;
    private final int cellsAboveTopBad;
    private final int semiBadCnt;

    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            int maxColumnHeight,
            int score,
            int combo,
            int cellsAboveTopBad,
            int semiBadCnt
    ) {
        this.badCnt = badCnt;
        this.flatRate = flatRate;
        this.holeCnt = holeCnt;
        this.maxColumnHeight = maxColumnHeight;
        this.score = score;
        this.combo = combo;
        this.cellsAboveTopBad = cellsAboveTopBad;
        this.semiBadCnt = semiBadCnt;
    }

    public boolean better(EvaluationState st) {
        if (st == null) {
            return true;
        }

        double x = getX();
        double stX = st.getX();

        if (x != stX) {
            return x < stX;
        }

        if (cellsAboveTopBad != st.cellsAboveTopBad) {
            return cellsAboveTopBad < st.cellsAboveTopBad;
        }

        if (flatRate != st.flatRate) {
            return flatRate < st.flatRate;
        }

        if (score != st.score) {
            return score > st.score;
        }

        if (combo != st.combo) {
            return combo > st.combo;
        }

        return false;
    }

    private double getX() {
        double x = badCnt;
        x += holeCnt;
        x += getHeightFactor(maxColumnHeight);
        x += semiBadCnt / 2.0;
        return x;
    }

    private static double sqr(double x) {
        return x * x;
    }

    @Override
    public String toString() {
        return "EvaluationState{" +
                "badCnt=" + badCnt +
                ", flatRate=" + flatRate +
                '}';
    }

    private static double getHeightFactor(int maxColumnHeight) {
        double heightRatio = maxColumnHeight / (double) Board.STANDARD_HEIGHT;
        return cube(heightRatio * HEIGHT_RATIO_Q);
    }

    private static double cube(double x) {
        return x * x * x;
    }
}

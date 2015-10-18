package tetris.logic;

import tetris.Board;

import static tetris.logic.EvaluationParameter.*;

public class EvaluationState {
    public static final EvaluationState LOST = new EvaluationState(0, 0, 0, 0, 0, 0, 0, 0, true, null);

    private final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;
    private final int cellsAboveTopBad;
    private final int semiBadCnt;
    private final boolean lost;

    private final double evaluation;

    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            int maxColumnHeight,
            int score,
            int combo,
            int cellsAboveTopBad,
            int semiBadCnt,
            boolean lost,
            ParameterWeights parameterWeight
    ) {
        this.badCnt = badCnt;
        this.flatRate = flatRate;
        this.holeCnt = holeCnt;
        this.maxColumnHeight = maxColumnHeight;
        this.score = score;
        this.combo = combo;
        this.cellsAboveTopBad = cellsAboveTopBad;
        this.semiBadCnt = semiBadCnt;
        this.lost = lost;
        this.evaluation = calcEvaluation(parameterWeight);
    }

    public boolean better(EvaluationState st) {
        if (st == null) {
            return true;
        }

        if (lost != st.lost) {
            return !lost;
        }

        if (evaluation != st.evaluation) {
            return evaluation < st.evaluation;
        }

        if (cellsAboveTopBad != st.cellsAboveTopBad) {
            return cellsAboveTopBad < st.cellsAboveTopBad;
        }

        if (flatRate != st.flatRate) {
            return flatRate < st.flatRate;
        }

        if (combo != st.combo) {
            return combo > st.combo;
        }

        return false;
    }

    private double calcEvaluation(ParameterWeights parameterWeight) {
        if (parameterWeight == null) {
            return 0;
        }
        double x = 0;
        x += badCnt * parameterWeight.get(BAD_CNT);
        x += holeCnt * parameterWeight.get(HOLE_CNT);
        x += getHeightFactor(maxColumnHeight, parameterWeight.get(HEIGHT));
        x += semiBadCnt * parameterWeight.get(SEMI_BAD_CNT);
        x += score * parameterWeight.get(SCORE);
        return x;
    }

    @Override
    public String toString() {
        return "EvaluationState{" +
                "badCnt=" + badCnt +
                ", flatRate=" + flatRate +
                '}';
    }

    private static double getHeightFactor(int maxColumnHeight, double heightRatioQ) {
        double heightRatio = maxColumnHeight / (double) Board.STANDARD_HEIGHT;
        return cube(heightRatio * heightRatioQ);
    }

    private static double cube(double x) {
        return x * x * x;
    }
}

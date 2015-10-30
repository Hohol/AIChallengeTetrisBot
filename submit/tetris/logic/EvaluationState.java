package tetris.logic;

import tetris.Board;

import static tetris.logic.EvaluationParameter.*;

public class EvaluationState {
    public static final EvaluationState LOST = new EvaluationState(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, true, null);

    private final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;
    private final int cellsAboveTopBad;
    private final int semiBadCnt;
    private final double prevStateEval;
    private final boolean lost;
    private final int skipCnt;
    public final boolean tSpinPattern;
    public final boolean semiTSpinPattern;

    final double evaluation;


    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            int maxColumnHeight,
            int score,
            int combo,
            int cellsAboveTopBad,
            int semiBadCnt,
            double prevStateEval,
            int skipCnt,
            boolean tSpinPattern,
            boolean semiTSpinPattern,
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
        this.prevStateEval = prevStateEval;
        this.skipCnt = skipCnt;
        this.tSpinPattern = tSpinPattern;
        this.semiTSpinPattern = semiTSpinPattern;
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

        return false;
    }

    private double calcEvaluation(ParameterWeights parameterWeight) {
        if (parameterWeight == null) {
            return 0;
        }
        double x = 0;
        x += badCnt * parameterWeight.get(BAD_CNT);
        x += holeCnt * parameterWeight.get(HOLE_CNT);
        x += getHeightFactor(maxColumnHeight, parameterWeight.get(HEIGHT), parameterWeight.get(HEIGHT_POW));
        x += semiBadCnt * parameterWeight.get(SEMI_BAD_CNT);
        x += score * parameterWeight.get(SCORE);
        x += cellsAboveTopBad * parameterWeight.get(CELLS_ABOVE_TOP);
        x += flatRate * parameterWeight.get(FLAT_RATE);
        x += combo * parameterWeight.get(COMBO);
        x += prevStateEval * parameterWeight.get(PREV_STATE);
        x += skipCnt * parameterWeight.get(SKIP_CNT);
        if (tSpinPattern) {
            x += parameterWeight.get(T_SPIN_PATTERN);
        }

        return x;
    }

    @Override
    public String toString() {
        return "EvaluationState{" +
                "badCnt=" + badCnt +
                ", flatRate=" + flatRate +
                '}';
    }

    private static double getHeightFactor(int maxColumnHeight, double heightRatioQ, double heightPow) {
        double heightRatio = maxColumnHeight / (double) Board.STANDARD_HEIGHT;
        return Math.pow(heightRatio * heightRatioQ, heightPow);
    }
}

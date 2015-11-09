package tetris.logic;

import tetris.Board;

import static tetris.logic.EvaluationParameter.*;

public class EvaluationState {
    public final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;
    public final int aboveBadFactor;
    private final int semiBadCnt;
    private final double prevStateEval;
    public final boolean lost;
    private final int skipCnt;
    public final int linesCleared;
    public final int monotonicRate;
    public final int lastRound;
    public final boolean tSpinPattern;
    public final boolean semiTSpinPattern;
    public final int iPatternFactor;

    final double evaluation;

    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            int maxColumnHeight,
            int score,
            int combo,
            int aboveBadFactor,
            int semiBadCnt,
            double prevStateEval,
            int skipCnt,
            int linesCleared,
            int monotonicRate,
            boolean tSpinPattern,
            boolean semiTSpinPattern,
            boolean lost,
            int lastRound,
            int iPatternFactor,
            ParameterWeights parameterWeight
    ) {
        this.badCnt = badCnt;
        this.flatRate = flatRate;
        this.holeCnt = holeCnt;
        this.maxColumnHeight = maxColumnHeight;
        this.score = score;
        this.combo = combo;
        this.aboveBadFactor = aboveBadFactor;
        this.semiBadCnt = semiBadCnt;
        this.prevStateEval = prevStateEval;
        this.skipCnt = skipCnt;
        this.linesCleared = linesCleared;
        this.monotonicRate = monotonicRate;
        this.tSpinPattern = tSpinPattern;
        this.semiTSpinPattern = semiTSpinPattern;
        this.lost = lost;
        this.lastRound = lastRound;
        this.iPatternFactor = iPatternFactor;
        this.evaluation = calcEvaluation(parameterWeight);
    }

    public boolean better(EvaluationState st) {
        if (st == null) {
            return true;
        }

        if (lost != st.lost) {
            return !lost;
        }
        if (lost) {
            if (lastRound != st.lastRound) {
                return lastRound > st.lastRound;
            }
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
        x += aboveBadFactor * parameterWeight.get(CELLS_ABOVE_TOP);
        x += flatRate * parameterWeight.get(FLAT_RATE);
        x += combo * parameterWeight.get(COMBO);
        x += prevStateEval * parameterWeight.get(PREV_STATE);
        x += skipCnt * parameterWeight.get(SKIP_CNT);
        x += monotonicRate * parameterWeight.get(MONOTONIC_RATE);
        x += iPatternFactor * parameterWeight.get(I_PATTERN);
        if (tSpinPattern) {
            x += parameterWeight.get(T_SPIN_PATTERN);
        }
        if (semiTSpinPattern) {
            x += parameterWeight.get(SEMI_T_SPIN_PATTERN);
        }
        if (linesCleared > 0 && score == 0) {
            x += parameterWeight.get(LOW_EFFICIENCY);
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

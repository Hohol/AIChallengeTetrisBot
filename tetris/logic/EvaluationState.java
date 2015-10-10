package tetris.logic;

public class EvaluationState {
    private final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final boolean tooHigh;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;
    private final int cellsAboveTopBad;

    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            boolean tooHigh,
            int maxColumnHeight,
            int score,
            int combo,
            int cellsAboveTopBad
    ) {
        this.badCnt = badCnt;
        this.flatRate = flatRate;
        this.holeCnt = holeCnt;
        this.tooHigh = tooHigh;
        this.maxColumnHeight = maxColumnHeight;
        this.score = score;
        this.combo = combo;
        this.cellsAboveTopBad = cellsAboveTopBad;
    }

    public boolean better(EvaluationState st) {
        if (st == null) {
            return true;
        }
        if (tooHigh != st.tooHigh) {
            return !tooHigh;
        }
        if (tooHigh) {
            if (maxColumnHeight != st.maxColumnHeight) {
                return maxColumnHeight < st.maxColumnHeight;
            }
        }

        if (badCnt != st.badCnt) {
            return badCnt < st.badCnt;
        }

        if (holeCnt != st.holeCnt) {
            return holeCnt < st.holeCnt;
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

    @Override
    public String toString() {
        return "EvaluationState{" +
                "badCnt=" + badCnt +
                ", flatRate=" + flatRate +
                '}';
    }
}

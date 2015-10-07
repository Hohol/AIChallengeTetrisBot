package tetris.logic;

// actually it represents not just state but difference between result state and start state
public class EvaluationState {
    private final int badCnt;
    private final int flatRate;
    private final int holeCnt;
    private final boolean tooHigh;
    private final int maxColumnHeight;
    private final int combo;
    private final int score;

    public EvaluationState(
            int badCnt,
            int flatRate,
            int holeCnt,
            boolean tooHigh,
            int maxColumnHeight,
            int score,
            int combo
    ) {
        this.badCnt = badCnt;
        this.flatRate = flatRate;
        this.holeCnt = holeCnt;
        this.tooHigh = tooHigh;
        this.maxColumnHeight = maxColumnHeight;
        this.score = score;
        this.combo = combo;
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

        if (score != st.score) {
            return score > st.score;
        }

        if (combo != st.combo) {
            return combo > st.combo;
        }

        if (badCnt != st.badCnt) {
            return badCnt < st.badCnt;
        }

        if (holeCnt != st.holeCnt) {
            return holeCnt < st.holeCnt;
        }

        if (flatRate != st.flatRate) {
            return flatRate < st.flatRate;
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

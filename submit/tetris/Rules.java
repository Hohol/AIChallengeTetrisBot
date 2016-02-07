package tetris;

public class Rules {

    public static final int SCORE_PER_GARBAGE = 3;

    private Rules() {
    }

    public static int calculateGarbage(int oldScore, int newScore) {
        return newScore / SCORE_PER_GARBAGE - oldScore / SCORE_PER_GARBAGE;
    }
}

package tetris;

public class DropResult {
    private final Board board;
    private final int scoreAdded;
    private final int newCombo;
    private final boolean skipAdded;

    public DropResult(Board board, int scoreAdded, int newCombo, boolean skipAdded) {
        this.board = board;
        this.scoreAdded = scoreAdded;
        this.newCombo = newCombo;
        this.skipAdded = skipAdded;
    }

    public Board getBoard() {
        return board;
    }

    public int getScoreAdded() {
        return scoreAdded;
    }

    public int getCombo() {
        return newCombo;
    }

    public boolean getSkipAdded() {
        return skipAdded;
    }
}

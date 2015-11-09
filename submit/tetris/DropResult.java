package tetris;

public class DropResult {
    private final Board board;
    private final int scoreAdded;
    private final int newCombo;
    private final int skipAdded;
    private final int linesCleared;
    private final boolean lost;

    public DropResult(Board board, int linesCleared, int scoreAdded, int newCombo, int skipAdded, boolean lost) {
        this.board = board;
        this.linesCleared = linesCleared;
        this.scoreAdded = scoreAdded;
        this.newCombo = newCombo;
        this.skipAdded = skipAdded;
        this.lost = lost;
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

    public int getSkipAdded() {
        return skipAdded;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public boolean isLost() {
        return lost;
    }
}

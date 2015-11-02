package tetris;

public class DropResult {
    private final Board board;
    private final int scoreAdded;
    private final int newCombo;
    private final int skipAdded;
    private final int linesCleared;

    public DropResult(Board board, int linesCleared, int scoreAdded, int newCombo, int skipAdded) {
        this.board = board;
        this.linesCleared = linesCleared;
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

    public int getSkipAdded() {
        return skipAdded;
    }

    public int getLinesCleared() {
        return linesCleared;
    }
}

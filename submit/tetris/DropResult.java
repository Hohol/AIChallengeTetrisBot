package tetris;

public class DropResult {
    private final Board board;
    private final int scoreAdded;
    private final int newCombo;

    public DropResult(Board board, int scoreAdded, int newCombo) {
        this.board = board;
        this.scoreAdded = scoreAdded;
        this.newCombo = newCombo;
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
}

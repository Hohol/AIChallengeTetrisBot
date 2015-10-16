package tetris;

public class DropResult {
    private final Board board;
    private final int scoreAdded;

    public DropResult(Board board, int scoreAdded) {
        this.board = board;
        this.scoreAdded = scoreAdded;
    }

    public Board getBoard() {
        return board;
    }

    public int getScoreAdded() {
        return scoreAdded;
    }
}

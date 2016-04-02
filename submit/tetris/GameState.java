package tetris;

import java.util.List;

public class GameState {
    private final Board board;
    private final TetriminoType nextTetrimino;
    private final TetriminoWithPosition fallingTetrimino;
    private final int combo;
    private final int round;
    private final int skipCnt;
    private final List<Integer> possibleGarbage; // doesn't actually belong here

    public GameState(
            Board board,
            TetriminoWithPosition fallingTetrimino,
            TetriminoType nextTetrimino,
            int combo,
            int round,
            int skipCnt,
            List<Integer> possibleGarbage
    ) {
        this.board = board;
        this.nextTetrimino = nextTetrimino;
        this.fallingTetrimino = fallingTetrimino;
        this.combo = combo;
        this.round = round;
        this.skipCnt = skipCnt;
        this.possibleGarbage = possibleGarbage;
    }

    public Board getBoard() {
        return board;
    }

    public TetriminoType getNextTetrimino() {
        return nextTetrimino;
    }

    public TetriminoWithPosition getFallingTetrimino() {
        return fallingTetrimino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState gameState = (GameState) o;

        if (!board.equals(gameState.board)) return false;
        if (fallingTetrimino != null ? !fallingTetrimino.equals(gameState.fallingTetrimino) : gameState.fallingTetrimino != null)
            return false;
        return nextTetrimino.equals(gameState.nextTetrimino);

    }

    @Override
    public int hashCode() {
        int result = board.hashCode();
        result = 31 * result + nextTetrimino.hashCode();
        result = 31 * result + (fallingTetrimino != null ? fallingTetrimino.hashCode() : 0);
        return result;
    }

    public int getCombo() {
        return combo;
    }

    public int getRound() {
        return round;
    }

    public int getSkipCnt() {
        return skipCnt;
    }

    public List<Integer> getPossibleGarbage() {
        return possibleGarbage;
    }
}
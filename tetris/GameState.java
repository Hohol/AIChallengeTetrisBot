package tetris;

public class GameState {
    private final Board board;
    private final Tetrimino nextTetrimino;
    private final TetriminoWithPosition fallingTetrimino;

    public GameState(Board board, TetriminoWithPosition fallingTetrimino, Tetrimino nextTetrimino) {
        this.board = board;
        this.nextTetrimino = nextTetrimino;
        this.fallingTetrimino = fallingTetrimino;
    }

    public Board getBoard() {
        return board;
    }

    public Tetrimino getNextTetrimino() {
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
}
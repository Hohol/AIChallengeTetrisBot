package tetris;

public class TetriminoWithPosition {
    private final int topRow, leftCol;
    private final Tetrimino tetrimino;

    public TetriminoWithPosition(int topRow, int leftCol, Tetrimino tetrimino) {
        this.topRow = topRow;
        this.leftCol = leftCol;
        this.tetrimino = tetrimino;
    }

    public TetriminoWithPosition(int topRow, int leftCol, TetriminoType type) {
        this(topRow, leftCol, Tetrimino.of(type));
    }

    public int getTopRow() {
        return topRow;
    }

    public int getLeftCol() {
        return leftCol;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TetriminoWithPosition that = (TetriminoWithPosition) o;

        if (leftCol != that.leftCol) return false;
        if (topRow != that.topRow) return false;
        if (!tetrimino.equals(that.tetrimino)) return false;

        return true;
    }

    public TetriminoWithPosition rotateCW() {
        return rotate(tetrimino.rotateCW());
    }

    public TetriminoWithPosition rotateCCW() {
        return rotate(tetrimino.rotateCCW());
    }

    private TetriminoWithPosition rotate(Tetrimino newTetrimino) {
        int newTopRow = topRow + tetrimino.getRowShift() - newTetrimino.getRowShift();
        int newLeftCol = leftCol + tetrimino.getColShift() - newTetrimino.getColShift();
        return new TetriminoWithPosition(newTopRow, newLeftCol, newTetrimino);
    }


    @Override
    public int hashCode() {
        int result = topRow;
        result = 31 * result + leftCol;
        result = 31 * result + tetrimino.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TetriminoWithPosition{" +
                "topRow=" + topRow +
                ", leftCol=" + leftCol +
                ", tetrimino=" + tetrimino +
                '}';
    }

    public TetriminoWithPosition moveLeft() {
        return new TetriminoWithPosition(topRow, leftCol - 1, tetrimino);
    }

    public TetriminoWithPosition moveRight() {
        return new TetriminoWithPosition(topRow, leftCol + 1, tetrimino);
    }

    public TetriminoWithPosition moveDown() {
        return new TetriminoWithPosition(topRow + 1, leftCol, tetrimino);
    }

    public TetriminoWithPosition move(Move move, Board board) {
        switch (move) {
            case LEFT:
                return moveLeft();
            case RIGHT:
                return moveRight();
            case DOWN:
                return moveDown();
            case DROP:
                TetriminoWithPosition r = this;
                while (!board.collides(r.moveDown())) {
                    r = r.moveDown();
                }
                return r;
            case ROTATE_CW:
                return rotateCW();
            case ROTATE_CCW:
                return rotateCCW();
            default:
                throw new RuntimeException();
        }
    }
}
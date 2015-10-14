package submit.tetris;

public class ColumnAndOrientation {
    private final int column;
    private final Tetrimino tetrimino;

    public ColumnAndOrientation(int column, Tetrimino tetrimino) {
        this.column = column;
        this.tetrimino = tetrimino;
    }

    public int getColumn() {
        return column;
    }

    public Tetrimino getTetrimino() {
        return tetrimino;
    }

    @Override
    public String toString() {
        return "ColumnAndOrientation{" +
                "column=" + column +
                ", tetrimino=" + tetrimino +
                '}';
    }
}

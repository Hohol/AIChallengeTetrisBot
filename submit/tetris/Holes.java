package tetris;

public class Holes {
    public final int firstCol;
    public final int secondCol;

    public Holes(int firstCol, int secondCol) {
        this.firstCol = firstCol;
        this.secondCol = secondCol;
    }

    public Holes(int firstCol) {
        this(firstCol, -1);
    }
}

package tetris;

public class Holes {
    public final int oneCol;
    public final int otherCol;

    public Holes(int oneCol, int otherCol) {
        this.oneCol = oneCol;
        this.otherCol = otherCol;
    }

    public Holes(int oneCol) {
        this(oneCol, -1);
    }
}

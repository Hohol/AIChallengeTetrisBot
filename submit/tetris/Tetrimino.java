package submit.tetris;

public class Tetrimino {

    public final static Tetrimino[][] ALL = new Tetrimino[TetriminoType.values().length][];

    static {
        for (TetriminoType type : TetriminoType.values()) {
            int ordinal = type.ordinal();
            if (type == TetriminoType.O) {
                ALL[ordinal] = new Tetrimino[1];
                ALL[ordinal][0] = new Tetrimino(type, type.b, 0, 0, 0);
            } else if (type == TetriminoType.I) {
                ALL[ordinal] = new Tetrimino[4];
                ALL[ordinal][0] = new Tetrimino(type, type.b, 0, -1, 0);
                ALL[ordinal][1] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][0].b), 1, 0, -2);
                ALL[ordinal][2] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][1].b), 2, -2, 0);
                ALL[ordinal][3] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][2].b), 3, 0, -1);
            } else {
                ALL[ordinal] = new Tetrimino[4];
                ALL[ordinal][0] = new Tetrimino(type, type.b, 0, 0, 0);
                ALL[ordinal][1] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][0].b), 1, 0, -1);
                ALL[ordinal][2] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][1].b), 2, -1, 0);
                ALL[ordinal][3] = new Tetrimino(type, rotateArrayCW(ALL[ordinal][2].b), 3, 0, 0);
            }
        }
    }

    private final TetriminoType type;
    private final boolean[][] b;
    private final int rowShift;
    private final int colShift;
    private final int orientation;

    public Tetrimino(TetriminoType type, boolean[][] b, int orientation, int rowShift, int colShift) {
        this.type = type;
        this.b = b;
        this.rowShift = rowShift;
        this.colShift = colShift;
        this.orientation = orientation;
    }

    public TetriminoType getType() {
        return type;
    }

    public int getRowShift() {
        return rowShift;
    }

    public int getColShift() {
        return colShift;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getWidth() {
        return b[0].length;
    }

    public int getHeight() {
        return b.length;
    }

    public boolean get(int row, int col) {
        return b[row][col];
    }

    private static boolean[][] rotateArrayCW(boolean[][] b) {
        boolean[][] newB = new boolean[b[0].length][b.length];
        for (int newRow = 0; newRow < newB.length; newRow++) {
            for (int newCol = 0; newCol < newB[0].length; newCol++) {
                newB[newRow][newCol] = b[newB[0].length - newCol - 1][newRow];
            }
        }
        return newB;
    }

    public Tetrimino rotateCW() {
        return ALL[type.ordinal()][(orientation + 1) % ALL[type.ordinal()].length];
    }

    public Tetrimino rotateCCW() {
        int len = ALL[type.ordinal()].length;
        return ALL[type.ordinal()][(orientation - 1 + len) % len];
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        r.append("{");
        for (boolean[] aB : b) {
            for (int j = 0; j < b[0].length; j++) {
                if (aB[j]) {
                    r.append("x");
                } else {
                    r.append(".");
                }
            }
            r.append("\n");
        }
        r.append("orientation=").append(orientation);
        r.append("}");
        return r.toString();
    }

    public static Tetrimino of(TetriminoType type) {
        return of(type, 0);
    }

    public static Tetrimino of(TetriminoType type, int orientation) {
        return ALL[type.ordinal()][orientation];
    }

    public int getOrientationsCnt() {
        return ALL[type.ordinal()].length;
    }
}
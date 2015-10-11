package tetris;

public class Board {

    public static int STANDARD_HEIGHT = 21;

    private final int height;
    private final int width;
    private final boolean b[][];
    private int penalty;

    public Board(int width, int height) {
        if (height != STANDARD_HEIGHT) {
            throw new RuntimeException("non-standard height");
        }
        this.height = height;
        this.width = width;
        b = new boolean[height][width];
    }

    private Board(Board board) {
        width = board.width;
        height = board.height;
        b = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(board.b[i], 0, b[i], 0, width);
        }
    }

    public Board(String s) {
        String[] a = s.split("\n");
        if (a.length < STANDARD_HEIGHT) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < a[0].length(); i++) {
                b.append(".");
            }
            String empty = b.toString();
            String[] tmp = new String[STANDARD_HEIGHT];
            int delta = STANDARD_HEIGHT - a.length;
            for (int i = 0; i < delta; i++) {
                tmp[i] = empty;
            }
            for (int i = 0; i < a.length; i++) {
                tmp[delta + i] = a[i];
            }
            a = tmp;
        } else if (a.length > STANDARD_HEIGHT) {
            throw new RuntimeException("non-standard board height");
        }
        height = a.length;
        width = a[0].length();
        b = new boolean[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                char ch = a[row].charAt(col);
                b[row][col] = (ch != '.');
                if (ch == 'o') {
                    if (penalty == 0) {
                        penalty = height - row;
                    }
                }
            }
        }
    }

    public void set(int row, int col, boolean value) {
        b[row][col] = value;
    }

    public boolean get(int row, int col) {
        return b[row][col];
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (b[row][col]) {
                    if (row >= height - penalty) {
                        r.append('o');
                    } else {
                        r.append("x");
                    }
                } else {
                    r.append(".");
                }
            }
            r.append("\n");
        }
        return r.toString();
    }

    public TetriminoWithPosition extractFallingTetrimino() {
        for (int topRow = 0; topRow < height; topRow++) {
            for (int leftCol = 0; leftCol < width; leftCol++) {
                for (Tetrimino[] tetriminoes : Tetrimino.ALL) {
                    for (Tetrimino tetrimino : tetriminoes) {
                        if (matches(topRow, leftCol, tetrimino)) {
                            clearTetrimino(topRow, leftCol, tetrimino);
                            return new TetriminoWithPosition(topRow, leftCol, tetrimino);
                        }
                    }
                }
            }
        }
        throw new RuntimeException("cant parse tetrimino");
    }

    private void clearTetrimino(int topRow, int leftCol, Tetrimino tetrimino) {
        for (int row = 0; row < tetrimino.getHeight(); row++) {
            for (int col = 0; col < tetrimino.getWidth(); col++) {
                if (tetrimino.get(row, col)) {
                    set(topRow + row, leftCol + col, false);
                }
            }
        }
    }

    private boolean matches(int topRow, int leftCol, Tetrimino tetrimino) {
        if (topRow + tetrimino.getHeight() - 1 >= height) {
            return false;
        }
        if (leftCol + tetrimino.getWidth() - 1 >= width) {
            return false;
        }
        for (int row = 0; row < tetrimino.getHeight(); row++) {
            for (int col = 0; col < tetrimino.getWidth(); col++) {
                if (tetrimino.get(row, col) != b[topRow + row][leftCol + col]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getWidth() {
        return width;
    }

    public DropResult drop(TetriminoWithPosition twp) {
        int leftCol = twp.getLeftCol();
        Tetrimino tetrimino = twp.getTetrimino();
        int topRow = twp.getTopRow();
        Board r = new Board(this);
        for (int i = 0; i < tetrimino.getHeight(); i++) {
            for (int j = 0; j < tetrimino.getWidth(); j++) {
                if (tetrimino.get(i, j)) {
                    if (topRow + i < 0) {
                        return null;
                    }
                    r.set(topRow + i, leftCol + j, true);
                }
            }
        }
        r.setPenalty(penalty);
        int linesCleared = r.clearFullRows();
        return new DropResult(r, linesCleared);
    }

    private int clearFullRows() {
        boolean[] full = new boolean[height];
        int linesCleared = 0;
        for (int row = 0; row < height - penalty; row++) {
            full[row] = true;
            for (int col = 0; col < width; col++) {
                if (!b[row][col]) {
                    full[row] = false;
                    break;
                }
            }
            if (full[row]) {
                linesCleared++;
            }
        }
        for (int col = 0; col < width; col++) {
            int botRow = height - 1;
            for (int row = height - 1; row >= 0; row--) {
                if (!full[row]) {
                    b[botRow][col] = b[row][col];
                    botRow--;
                }
            }
            while (botRow >= 0) {
                b[botRow][col] = false;
                botRow--;
            }
        }
        return linesCleared;
    }

    public int getTopRowInColumn(int col) {
        int boardTopRow = height; // if empty column
        for (int i = 0; i < height; i++) {
            if (b[i][col]) {
                boardTopRow = i;
                break;
            }
        }
        return boardTopRow;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (height != board.height) return false;
        if (width != board.width) return false;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (b[i][j] != board.b[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getColumnHeight(int col) {
        return getHeight() - getTopRowInColumn(col);
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
}

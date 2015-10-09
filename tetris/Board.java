package tetris;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Board {

    private final int height;
    private final int width;
    private final boolean b[][];
    private int penalty;

    public Board(int width, int height) {
        this.height = height;
        this.width = width;
        b = new boolean[height][width];
    }

    public Board(Board board) {
        width = board.width;
        height = board.height;
        b = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(board.b[i], 0, b[i], 0, width);
        }
    }

    public Board(String s) {
        String[] a = s.split("\n");
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
        int cnt = 0;
        int topRow = 999;
        int leftCol = 999;
        int bottomRow = 0;
        int rightCol = 0;
        fori:
        for (int i = 0; i < height / 2; i++) {
            boolean foundOnLine = false;
            for (int j = 0; j < width; j++) {
                if (b[i][j]) {
                    foundOnLine = true;
                    cnt++;
                    topRow = min(topRow, i);
                    leftCol = min(leftCol, j);
                    bottomRow = max(bottomRow, i);
                    rightCol = max(rightCol, j);
                    if (cnt == 4) {
                        break fori;
                    }
                }
            }
            if (cnt > 0 && !foundOnLine) {
                return null;
            }
        }
        if (cnt != 4) {
            return null;
        }
        boolean[][] b = new boolean[bottomRow - topRow + 1][rightCol - leftCol + 1];
        for (int i = topRow; i <= bottomRow; i++) {
            for (int j = leftCol; j <= rightCol; j++) {
                b[i - topRow][j - leftCol] = this.b[i][j];
                this.b[i][j] = false;
            }
        }
        return new TetriminoWithPosition(topRow, leftCol, new Tetrimino(b));
    }

    public int getWidth() {
        return width;
    }

    public DropResult drop(Tetrimino tetrimino, int leftCol) {
        int minNewTopTetriminoRow = 999;
        for (int j = 0; j < tetrimino.getWidth(); j++) {
            int tetriminoBottomRow = 0;
            for (int i = tetrimino.getHeight() - 1; i >= 0; i--) {
                if (tetrimino.get(i, j)) {
                    tetriminoBottomRow = i;
                    break;
                }
            }
            int curCol = leftCol + j;

            int boardTopRow = getTopRowInColumn(curCol);
            int newTopTetriminoRow = boardTopRow - tetriminoBottomRow - 1;
            minNewTopTetriminoRow = min(minNewTopTetriminoRow, newTopTetriminoRow);
        }

        Board r = new Board(this);
        for (int i = 0; i < tetrimino.getHeight(); i++) {
            for (int j = 0; j < tetrimino.getWidth(); j++) {
                if (tetrimino.get(i, j)) {
                    if (minNewTopTetriminoRow + i < 0) {
                        return null;
                    }
                    r.set(minNewTopTetriminoRow + i, leftCol + j, true);
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

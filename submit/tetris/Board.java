package tetris;

public class Board {

    public static final int SOLID_BLOCK_PERIOD = 15;
    public static int STANDARD_HEIGHT = 21;
    public static int STANDARD_WIDTH = 10;

    private final int height;
    private final int width;
    private final boolean b[][];
    private int penalty;

    public Board(int height, int width) {
        if (height != STANDARD_HEIGHT) {
            throw new RuntimeException("non-standard height");
        }
        this.height = height;
        this.width = width;
        b = new boolean[height][width];
    }

    public Board(Board board) {
        width = board.width;
        height = board.height;
        b = new boolean[height][width];
        this.penalty = board.penalty;
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

    public DropResult drop(TetriminoWithPosition twp, Move lastMove, int combo, int round) {
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
        r.addPenaltyIfNeeded(round);
        int linesCleared = r.clearFullRows();

        boolean wasTSpin = wasTSpin(twp, lastMove, linesCleared);

        int newCombo;
        if (linesCleared == 0) {
            newCombo = 0;
        } else if (linesCleared >= 2 || wasTSpin) {
            newCombo = combo + 1;
        } else { // linesCleared == 1
            newCombo = combo;
        }

        int scoreDelta;
        if (r.getMaxColumnHeight() == penalty) { // perfect clear
            scoreDelta = 18;
        } else {
            scoreDelta = getScore(linesCleared, combo, wasTSpin);
        }

        int skipAdded = (wasTSpin && linesCleared == 2 || linesCleared == 4) ? 1 : 0;

        return new DropResult(r, linesCleared, scoreDelta, newCombo, skipAdded);
    }

    public DropResult skipMove(int combo, int round) {
        Board r = new Board(this);
        r.addPenaltyIfNeeded(round);
        return new DropResult(r, 0, combo, combo, -1);
    }

    private void addPenaltyIfNeeded(int round) {
        if (round % SOLID_BLOCK_PERIOD == 0) {
            addPenalty();
        }
    }

    private int getScore(int linesCleared, int comboScore, boolean wasTSpin) {
        if (linesCleared == 0) {
            return 0;
        }
        if (wasTSpin) {
            if (linesCleared == 1) {
                return 5 + comboScore;
            } else if (linesCleared == 2) {
                return 10 + comboScore;
            } else {
                throw new RuntimeException();
            }
        }
        if (linesCleared == 1) {
            return comboScore;
        }
        if (linesCleared == 2) {
            return 3 + comboScore;
        }
        if (linesCleared == 3) {
            return 6 + comboScore;
        }
        if (linesCleared == 4) {
            return 10 + comboScore;
        }
        throw new RuntimeException();
    }

    private boolean wasTSpin(TetriminoWithPosition finalPosition, Move lastMove, int linesCleared) {
        if (linesCleared == 0) {
            return false;
        }
        Tetrimino t = finalPosition.getTetrimino();
        if (t.getType() != TetriminoType.T) {
            return false;
        }
        if (lastMove != Move.ROTATE_CW && lastMove != Move.ROTATE_CCW) {
            return false;
        }
        int r = finalPosition.getTopRow() + t.getRowShift();
        int c = finalPosition.getLeftCol() + t.getColShift();
        int cnt = 0;
        if (get(r, c)) {
            cnt++;
        }
        if (get(r + 2, c)) {
            cnt++;
        }
        if (get(r, c + 2)) {
            cnt++;
        }
        if (get(r + 2, c + 2)) {
            cnt++;
        }
        return cnt == 3;
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

    public int getMaxColumnHeight() {
        int r = 0;
        for (int col = 0; col < width; col++) {
            r = Math.max(r, getColumnHeight(col));
        }
        return r;
    }

    public TetriminoWithPosition newFallingTetrimino(TetriminoType type) {
        if (height == STANDARD_HEIGHT) {
            int leftCol = width / 2 - (type == TetriminoType.O ? 1 : 2);
            int topRow = type == TetriminoType.I ? 1 : 0;
            return new TetriminoWithPosition(topRow, leftCol, Tetrimino.of(type));
        } else { // to simplify tests logic
            return new TetriminoWithPosition(0, 0, Tetrimino.of(type));
        }
    }

    public boolean collides(TetriminoWithPosition p) {
        if (p.getLeftCol() < 0) {
            return true;
        }
        Tetrimino t = p.getTetrimino();
        if (p.getLeftCol() + t.getWidth() - 1 >= getWidth()) {
            return true;
        }
        if (p.getTopRow() + t.getHeight() - 1 >= getHeight()) {
            return true;
        }
        for (int row = 0; row < t.getHeight(); row++) {
            for (int col = 0; col < t.getWidth(); col++) {
                if (t.get(row, col) && get(p.getTopRow() + row, p.getLeftCol() + col)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addPenalty() {
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                b[row][col] = b[row + 1][col];
            }
        }
        for (int col = 0; col < width; col++) {
            b[height - 1][col] = true;
        }
        penalty++;
    }

    public void addGarbage(Holes... holes) {
        int linesAdded = holes.length;
        for (int row = 0; row < height - penalty - linesAdded; row++) {
            for (int col = 0; col < width; col++) {
                b[row][col] = b[row + linesAdded][col];
            }
        }
        for (int row = 0; row < linesAdded; row++) {
            for (int col = 0; col < width; col++) {
                int realRow = height - penalty - linesAdded + row;
                if (col == holes[row].oneCol || col == holes[row].otherCol) {
                    b[realRow][col] = false;
                } else {
                    b[realRow][col] = true;
                }
            }
        }
    }

    public int blocksInRowCnt(int row) {
        int r = 0;
        for (int col = 0; col < width; col++) {
            if (get(row, col)) {
                r++;
            }
        }
        return r;
    }
}

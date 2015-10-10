package tetris.logic;

import tetris.*;

import java.util.*;

public class BestMoveFinder {

    private final Evaluator evaluator = new Evaluator();

    public List<Move> findBestMoves(GameState gameState) {
        List<Move> moves = findBestMoves(gameState.getBoard(), gameState.getFallingTetrimino(), gameState.getNextTetrimino(), 0, gameState.getCombo()).getMoves();
        Collections.reverse(moves);
        while (moves.size() > 0 && moves.get(moves.size() - 1) == Move.DOWN) {
            moves.remove(moves.size() - 1);
        }
        moves.add(Move.DROP);
        return moves;
    }

    private MovesWithEvaluation findBestMoves(Board board, TetriminoWithPosition fallingTetrimino, Tetrimino nextTetrimino, int score, int combo) {
        EvaluationState bestState = null;
        TetriminoWithPosition bestPosition = null;

        TetriminoWithPosition[][][] bfs = bfs(board, fallingTetrimino);
        List<TetriminoWithPosition> availableFinalPositions = new ArrayList<>();
        for (int row = 0; row < bfs.length; row++) {
            for (int col = 0; col < bfs[0].length; col++) {
                for (int orientation = 0; orientation < bfs[0][0].length; orientation++) {
                    if (bfs[row][col][orientation] == null) {
                        continue;
                    }
                    TetriminoWithPosition t = new TetriminoWithPosition(row, col, Tetrimino.of(fallingTetrimino.getTetrimino().getType(), orientation));
                    if (collides(board, t.moveDown())) {
                        availableFinalPositions.add(t);
                    }
                }
            }
        }

        for (TetriminoWithPosition finalPosition : availableFinalPositions) {
            DropResult dropResult = board.drop(finalPosition);
            Board newBoard = dropResult.getBoard();

            int scoreDelta = getScore(dropResult.getLinesCleared(), combo);
            int newScore = score + scoreDelta;
            int newCombo = dropResult.getLinesCleared() > 0 ? combo + 1 : 0;

            EvaluationState curState;

            if (nextTetrimino == null) {
                curState = evaluator.getEvaluation(newBoard, newScore, newCombo);
            } else {
                int nextTopRow = nextTetrimino.getType() == TetriminoType.I ? 1 : 0;
                TetriminoWithPosition nextTwp = new TetriminoWithPosition(nextTopRow, getFallingCol(board.getWidth(), nextTetrimino.getWidth()), nextTetrimino);
                curState = findBestMoves(newBoard, nextTwp, null, newScore, newCombo).getState();
            }
            if (curState != null && curState.better(bestState)) {
                bestState = curState;
                bestPosition = finalPosition;
            }
        }
        List<Move> moves = new ArrayList<>();
        TetriminoWithPosition cur = bestPosition;
        while (!cur.equals(fallingTetrimino)) {
            TetriminoWithPosition prev = bfs[cur.getTopRow()][cur.getLeftCol()][cur.getTetrimino().getOrientation()];
            if (prev.moveLeft().equals(cur)) {
                moves.add(Move.LEFT);
            } else if (prev.moveRight().equals(cur)) {
                moves.add(Move.RIGHT);
            } else if (prev.moveDown().equals(cur)) {
                moves.add(Move.DOWN);
            } else if (prev.rotateCW().equals(cur)) {
                moves.add(Move.ROTATE_CW);
            } else if (prev.rotateCCW().equals(cur)) {
                moves.add(Move.ROTATE_CCW);
            } else {
                throw new RuntimeException("no move from transforms " + prev + " to " + cur);
            }
            cur = prev;
        }
        return new MovesWithEvaluation(moves, bestState);
    }

    public static int getFallingCol(int boardWidth, int tetriminoWidth) {
        if (tetriminoWidth == 2) {
            return boardWidth / 2 - 1;
        } else {
            return boardWidth / 2 - 2;
        }
    }

    private TetriminoWithPosition[][][] bfs(Board board, TetriminoWithPosition t) {
        TetriminoWithPosition[][][] from = new TetriminoWithPosition[board.getHeight()][board.getWidth()][t.getTetrimino().getOrientationsCnt()];
        from[t.getTopRow()][t.getLeftCol()][t.getTetrimino().getOrientation()] = t;
        Queue<TetriminoWithPosition> q = new ArrayDeque<>();
        q.add(t);
        while (!q.isEmpty()) {
            t = q.remove();
            List<TetriminoWithPosition> nextPositions = new ArrayList<>(); // todo not recreate?
            nextPositions.add(t.rotateCW());
            nextPositions.add(t.rotateCCW());
            nextPositions.add(t.moveLeft());
            nextPositions.add(t.moveRight());
            nextPositions.add(t.moveDown());
            for (TetriminoWithPosition p : nextPositions) {
                if (collides(board, p)) {
                    continue;
                }
                if (from[p.getTopRow()][p.getLeftCol()][p.getTetrimino().getOrientation()] != null) {
                    continue;
                }
                from[p.getTopRow()][p.getLeftCol()][p.getTetrimino().getOrientation()] = t;
                q.add(p);
            }
        }
        return from;
    }

    private boolean collides(Board board, TetriminoWithPosition p) {
        if (p.getLeftCol() < 0) {
            return true;
        }
        Tetrimino t = p.getTetrimino();
        if (p.getLeftCol() + t.getWidth() - 1 >= board.getWidth()) {
            return true;
        }
        if (p.getTopRow() + t.getHeight() - 1 >= board.getHeight()) {
            return true;
        }
        for (int row = 0; row < t.getHeight(); row++) {
            for (int col = 0; col < t.getWidth(); col++) {
                if (t.get(row, col) && board.get(p.getTopRow() + row, p.getLeftCol() + col)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getScore(int linesCleared, int comboBefore) {
        if (linesCleared == 0) {
            return 0;
        }
        if (linesCleared == 1) {
            return 1 + comboBefore;
        }
        if (linesCleared == 2) {
            return 3 + comboBefore;
        }
        if (linesCleared == 3) {
            return 6 + comboBefore;
        }
        if (linesCleared == 4) {
            return 12 + comboBefore;
        }
        throw new RuntimeException();
    }
}
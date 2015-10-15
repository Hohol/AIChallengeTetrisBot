package tetris.logic;

import tetris.*;

import java.util.*;

public class BestMoveFinder {

    private final Evaluator evaluator = new Evaluator();

    public List<Move> findBestMoves(GameState gameState) {
        List<Move> moves = findBestMoves(gameState.getBoard(), gameState.getFallingTetrimino(), gameState.getNextTetrimino(), 0, gameState.getCombo()).getMoves();
        Collections.reverse(moves);
        boolean removedSomeDowns = false;
        while (moves.size() > 0 && moves.get(moves.size() - 1) == Move.DOWN) {
            moves.remove(moves.size() - 1);
            removedSomeDowns = true;
        }
        if (removedSomeDowns) {
            moves.add(Move.DROP);
        }
        return moves;
    }

    private MovesWithEvaluation findBestMoves(Board board, TetriminoWithPosition fallingTetrimino, TetriminoType nextTetrimino, int score, int combo) {
        if (board.collides(fallingTetrimino)) {
            return new MovesWithEvaluation(null, null);
        }
        EvaluationState bestState = null;
        TetriminoWithPosition bestPosition = null;

        TetriminoWithPosition[][][] bfs = bfs(board, fallingTetrimino);
        List<TetriminoWithPosition> availableFinalPositions = new ArrayList<>();
        for (int row = bfs.length - 1; row >= 0; row--) {
            for (int col = 0; col < bfs[0].length; col++) {
                for (int orientation = 0; orientation < bfs[0][0].length; orientation++) {
                    if (bfs[row][col][orientation] == null) {
                        continue;
                    }
                    TetriminoWithPosition t = new TetriminoWithPosition(row, col, Tetrimino.of(fallingTetrimino.getTetrimino().getType(), orientation));
                    if (board.collides(t.moveDown())) {
                        availableFinalPositions.add(t);
                    }
                }
            }
        }

        for (TetriminoWithPosition finalPosition : availableFinalPositions) {
            DropResult dropResult = board.drop(finalPosition);
            Board newBoard = dropResult.getBoard();
            if (newBoard.getMaxColumnHeight() == board.getHeight()) {
                continue;
            }

            boolean wasTSpin = wasTSpin(board, finalPosition, bfs, dropResult.getLinesCleared());

            int scoreDelta = getScore(dropResult.getLinesCleared(), combo, wasTSpin);
            int newScore = score + scoreDelta;
            int newCombo = dropResult.getLinesCleared() > 0 ? combo + 1 : 0;

            EvaluationState curState;

            if (nextTetrimino == null) {
                curState = evaluator.getEvaluation(newBoard, newScore, newCombo);
            } else {
                TetriminoWithPosition nextTwp = newBoard.newFallingTetrimino(nextTetrimino);
                curState = findBestMoves(newBoard, nextTwp, null, newScore, newCombo).getState();
            }
            if (curState != null && curState.better(bestState)) {
                bestState = curState;
                bestPosition = finalPosition;
            }
        }
        if (bestPosition == null) {
            return new MovesWithEvaluation(null, null);
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

    private boolean wasTSpin(Board board, TetriminoWithPosition finalPosition, TetriminoWithPosition[][][] from, int linesCleared) {
        if (linesCleared == 0) {
            return false;
        }
        Tetrimino t = finalPosition.getTetrimino();
        if (t.getType() != TetriminoType.T) {
            return false;
        }
        TetriminoWithPosition prev = from[finalPosition.getTopRow()][finalPosition.getLeftCol()][t.getOrientation()];
        if (t.getOrientation() == prev.getTetrimino().getOrientation()) { // no rotation
            return false;
        }
        int r = finalPosition.getTopRow() + t.getRowShift();
        int c = finalPosition.getLeftCol() + t.getColShift();
        int cnt = 0;
        if (board.get(r, c)) {
            cnt++;
        }
        if (board.get(r + 2, c)) {
            cnt++;
        }
        if (board.get(r, c + 2)) {
            cnt++;
        }
        if (board.get(r + 2, c + 2)) {
            cnt++;
        }
        return cnt == 3;
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
                if (board.collides(p)) {
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

    private int getScore(int linesCleared, int comboBefore, boolean wasTSpin) {
        if (linesCleared == 0) {
            return 0;
        }
        if (wasTSpin) {
            if (linesCleared == 1) {
                return 6 + comboBefore;
            } else if (linesCleared == 2) {
                return 12 + comboBefore;
            } else {
                throw new RuntimeException();
            }
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

    @SuppressWarnings("unused")
    private static boolean stopOn(TetriminoWithPosition twp, TetriminoType type, int row, int col, int orientation) {
        return twp.getTopRow() == row && twp.getLeftCol() == col && twp.getTetrimino().getType() == type && twp.getTetrimino().getOrientation() == orientation;
    }
}
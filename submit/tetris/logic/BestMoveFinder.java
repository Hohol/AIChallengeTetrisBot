package tetris.logic;

import tetris.*;

import java.util.*;

import static tetris.Move.*;
import static tetris.logic.EvaluationParameter.*;

public class BestMoveFinder {

    public static final ParameterWeights BEST_PARAMETERS = new ParameterWeights()
            .put(BAD_CNT, 5.582919899887908).put(HOLE_CNT, 2.878471664579383).put(HEIGHT, 1.4631383737117991).put(SEMI_BAD_CNT, 2.133187913129006).put(SCORE, -0.716393996010999).put(HEIGHT_POW, 4.672254358240745).put(CELLS_ABOVE_TOP, 0.8587277875132031).put(FLAT_RATE, 0.733147384274665).put(COMBO, -0.16547251812410724)
            .put(PREV_STATE, 0.2);

    private final Evaluator evaluator;

    public static BestMoveFinder getBest() {
        return new BestMoveFinder(BEST_PARAMETERS);
    }

    public BestMoveFinder(ParameterWeights parameterWeight) {
        this.evaluator = new Evaluator(parameterWeight);
    }

    public List<Move> findBestMoves(GameState gameState) {
        Board board = gameState.getBoard();
        List<Move> moves = findBestMoves(board, gameState.getFallingTetrimino(), gameState.getNextTetrimino(), 0, gameState.getCombo(), gameState.getRound(), 0, gameState.getSkipCnt()).getMoves();
        Collections.reverse(moves);
        boolean removedSomeDowns = false;
        while (moves.size() > 0 && moves.get(moves.size() - 1) == DOWN) {
            moves.remove(moves.size() - 1);
            removedSomeDowns = true;
        }
        if (removedSomeDowns) {
            moves.add(DROP);
        }
        return moves;
    }

    private MovesWithEvaluation findBestMoves(Board board, TetriminoWithPosition fallingTetrimino, TetriminoType nextTetrimino, int score, int combo, int round, double prevStateEval, int skipCnt) {
        if (board.collides(fallingTetrimino)) {
            return new MovesWithEvaluation(null, EvaluationState.LOST);
        }
        EvaluationState bestState = null;
        TetriminoWithPosition bestPosition = null;

        if (skipCnt > 0) {
            Board newBoard = new Board(board);
            newBoard.addPenaltyIfNeeded(round);
            if (nextTetrimino == null) {
                bestState = evaluator.getEvaluation(newBoard, score, combo, prevStateEval);
            } else {
                double curEvaluation = evaluator.getEvaluation(newBoard, score, combo, prevStateEval).evaluation;
                bestState = findBestMoves(newBoard, newBoard.newFallingTetrimino(nextTetrimino), null, score, combo, round + 1, curEvaluation, skipCnt - 1).getState();
            }
        }

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
            DropResult dropResult = board.drop(finalPosition, getPrevMove(
                            finalPosition,
                            bfs[finalPosition.getTopRow()][finalPosition.getLeftCol()][finalPosition.getTetrimino().getOrientation()]),
                    combo,
                    round
            );
            Board newBoard = dropResult.getBoard();
            if (newBoard.getMaxColumnHeight() == board.getHeight()) {
                continue;
            }

            int newScore = score + dropResult.getScoreAdded();
            int newCombo = dropResult.getCombo();
            int newSkipCnt = skipCnt;
            if (dropResult.getSkipAdded()) {
                newSkipCnt++;
            }

            EvaluationState curState;

            if (nextTetrimino == null) {
                curState = evaluator.getEvaluation(newBoard, newScore, newCombo, prevStateEval);
            } else {
                TetriminoWithPosition nextTwp = newBoard.newFallingTetrimino(nextTetrimino);
                double curStateEvaluation = evaluator.getEvaluation(newBoard, newScore, newCombo, prevStateEval).evaluation;
                curState = findBestMoves(newBoard, nextTwp, null, newScore, newCombo, round + 1, curStateEvaluation, newSkipCnt).getState();
            }
            if (curState != null && curState.better(bestState)) {
                bestState = curState;
                bestPosition = finalPosition;
            }
        }
        if (bestPosition == null && bestState != null) { // Skip was the best move. Warning! Very ugly code!
            return new MovesWithEvaluation(Collections.singletonList(SKIP), bestState);
        }
        if (bestPosition == null) {
            return new MovesWithEvaluation(Collections.emptyList(), EvaluationState.LOST);
        }
        List<Move> moves = new ArrayList<>();
        TetriminoWithPosition cur = bestPosition;
        while (!cur.equals(fallingTetrimino)) {
            TetriminoWithPosition prev = bfs[cur.getTopRow()][cur.getLeftCol()][cur.getTetrimino().getOrientation()];
            Move prevMove = getPrevMove(cur, prev);
            moves.add(prevMove);
            cur = prev;
        }
        return new MovesWithEvaluation(moves, bestState);
    }

    private Move getPrevMove(TetriminoWithPosition cur, TetriminoWithPosition prev) {
        Move prevMove;
        if (prev.moveLeft().equals(cur)) {
            prevMove = LEFT;
        } else if (prev.moveRight().equals(cur)) {
            prevMove = RIGHT;
        } else if (prev.moveDown().equals(cur)) {
            prevMove = DOWN;
        } else if (prev.rotateCW().equals(cur)) {
            prevMove = ROTATE_CW;
        } else if (prev.rotateCCW().equals(cur)) {
            prevMove = ROTATE_CCW;
        } else if (prev.equals(cur)) {
            return null;
        } else {
            throw new RuntimeException("no move transforms " + prev + " to " + cur);
        }
        return prevMove;
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

    @SuppressWarnings("unused")
    private static boolean stopOn(TetriminoWithPosition twp, TetriminoType type, int row, int col, int orientation) {
        return twp.getTopRow() == row && twp.getLeftCol() == col && twp.getTetrimino().getType() == type && twp.getTetrimino().getOrientation() == orientation;
    }
}
package tetris.logic;

import tetris.*;

import java.util.*;

import static tetris.Move.*;
import static tetris.TetriminoType.*;
import static tetris.logic.EvaluationParameter.*;

public class BestMoveFinder {

    public static final ParameterWeights BEST_PARAMETERS = new ParameterWeights()
            .put(BAD_CNT, 17.064971771382382).put(HOLE_CNT, 5.733177366629449).put(HEIGHT, 1.4076593440352523).put(SEMI_BAD_CNT, 5.076315939386729).put(SCORE, -2.2862666329869157).put(HEIGHT_POW, 8.751944794972278).put(CELLS_ABOVE_TOP, 0.23822656903576234).put(FLAT_RATE, 0.6922331319044679).put(COMBO, -0.3614692532280521).put(PREV_STATE, 0.017307843006568957).put(SKIP_CNT, -7.438704615109288).put(T_SPIN_PATTERN, -12.47511154380235).put(SEMI_T_SPIN_PATTERN, -2.3599588643506335).put(LOW_EFFICIENCY, 12.290719514160582).put(MONOTONIC_RATE, 2.1585022287073583).put(I_PATTERN, -5.377388018547392);

    private final Evaluator evaluator;

    public static BestMoveFinder getBest() {
        return new BestMoveFinder(BEST_PARAMETERS);
    }

    public BestMoveFinder(ParameterWeights parameterWeight) {
        this.evaluator = new Evaluator(parameterWeight);
    }

    public List<Move> findBestMoves(GameState gameState) {
        Board board = gameState.getBoard();
        List<Move> moves = findBestMoves(
                new GameState2(
                        board,
                        gameState.getFallingTetrimino(),
                        gameState.getNextTetrimino(),
                        0,
                        gameState.getCombo(),
                        gameState.getRound(),
                        0,
                        gameState.getSkipCnt(),
                        gameState.getPossibleGarbage(),
                        0
                )
        ).getMoves();
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

    private MovesWithEvaluation findBestMoves(GameState2 gameState) {
        final Board board = gameState.board;
        final TetriminoWithPosition fallingTetrimino = gameState.fallingTetrimino;
        final TetriminoType nextTetrimino = gameState.nextTetrimino;
        final int score = gameState.score;
        final int combo = gameState.combo;
        final int round = gameState.round;
        final double prevStateEval = gameState.prevStateEval;
        final int skipCnt = gameState.skipCnt;
        final int possibleGarbage = gameState.possibleGarbage;
        final int linesCleared = gameState.linesCleared;
        if (board.collides(fallingTetrimino)) {
            return new MovesWithEvaluation(
                    null,
                    evaluator.getEvaluation(board, score, 0, prevStateEval, 0, linesCleared, true, round)
            );
        }
        EvaluationState bestState = null;
        TetriminoWithPosition bestPosition = null;

        if (skipCnt > 0) {
            Board newBoard = board.skipMove(score, round).getBoard();
            for (int i = 0; i < possibleGarbage; i++) {
                newBoard.addPenalty();
            }
            EvaluationState curEvaluation = evaluator.getEvaluation(newBoard, score, combo, prevStateEval, skipCnt - 1, 0, false, round);
            if (nextTetrimino == null || curEvaluation.lost) {
                bestState = curEvaluation;
            } else {
                bestState = findBestMoves(new GameState2(
                                newBoard,
                                newBoard.newFallingTetrimino(nextTetrimino),
                                null,
                                score,
                                combo,
                                round + 1,
                                curEvaluation.evaluation,
                                skipCnt - 1,
                                0,
                                linesCleared
                        )
                ).getState();
            }
        }

        TetriminoWithPosition[][][] bfs = PathFinder.bfs(board, fallingTetrimino);
        List<TetriminoWithPosition> availableFinalPositions = new ArrayList<>();
        TetriminoType type = fallingTetrimino.getTetrimino().getType();
        for (int row = bfs.length - 1; row >= 0; row--) {
            for (int col = 0; col < bfs[0].length; col++) {
                for (int orientation = 0; orientation < bfs[0][0].length; orientation++) {
                    if (bfs[row][col][orientation] == null) {
                        continue;
                    }
                    if ((orientation == 2 || orientation == 3)
                            && (type == I || type == S || type == Z)
                            && bfs[row][col][orientation - 2] != null) {
                        continue;
                    }
                    TetriminoWithPosition t = new TetriminoWithPosition(row, col, Tetrimino.of(type, orientation));
                    if (board.collides(t.moveDown())) {
                        availableFinalPositions.add(t);
                    }
                }
            }
        }

        for (TetriminoWithPosition finalPosition : availableFinalPositions) {
            DropResult dropResult = board.drop(finalPosition, PathFinder.getPrevMove(
                            finalPosition,
                            bfs[finalPosition.getTopRow()][finalPosition.getLeftCol()][finalPosition.getTetrimino().getOrientation()]),
                    combo,
                    round
            );
            Board newBoard = dropResult.getBoard();
            for (int i = 0; i < possibleGarbage; i++) {
                newBoard.addPenalty();
            }

            int newScore = score + dropResult.getScoreAdded();
            int newCombo = dropResult.getCombo();
            int newSkipCnt = skipCnt;
            newSkipCnt += dropResult.getSkipAdded();
            int newLinesCleared = linesCleared + dropResult.getLinesCleared();

            EvaluationState curState;

            EvaluationState curEvaluation = evaluator.getEvaluation(
                    newBoard,
                    newScore,
                    newCombo,
                    prevStateEval,
                    newSkipCnt,
                    newLinesCleared,
                    dropResult.isLost(),
                    round
            );
            if (nextTetrimino == null || curEvaluation.lost) {
                curState = curEvaluation;
            } else {
                TetriminoWithPosition nextTwp = newBoard.newFallingTetrimino(nextTetrimino);
                curState = findBestMoves(
                        new GameState2(
                                newBoard,
                                nextTwp,
                                null,
                                newScore,
                                newCombo,
                                round + 1,
                                curEvaluation.evaluation,
                                newSkipCnt,
                                0,
                                newLinesCleared
                        )).getState();
            }
            if (bestState == null || curState.better(bestState)) {
                bestState = curState;
                bestPosition = finalPosition;
            }
        }
        if (bestPosition == null && bestState != null) { // Skip was the best move. Warning! Very ugly code!
            return new MovesWithEvaluation(Collections.singletonList(SKIP), bestState);
        }
        if (bestPosition == null) {
            return new MovesWithEvaluation(
                    Collections.emptyList(),
                    evaluator.getEvaluation(board, score, 0, prevStateEval, 0, linesCleared, true, round)
            );
        }
        List<Move> moves = new ArrayList<>();
        TetriminoWithPosition cur = bestPosition;
        while (!cur.equals(fallingTetrimino)) {
            TetriminoWithPosition prev = bfs[cur.getTopRow()][cur.getLeftCol()][cur.getTetrimino().getOrientation()];
            Move prevMove = PathFinder.getPrevMove(cur, prev);
            moves.add(prevMove);
            cur = prev;
        }
        return new MovesWithEvaluation(moves, bestState);
    }

    @SuppressWarnings("unused")
    private static boolean stopOn(TetriminoWithPosition twp, TetriminoType type, int row, int col, int orientation) {
        return twp.getTopRow() == row && twp.getLeftCol() == col && twp.getTetrimino().getType() == type && twp.getTetrimino().getOrientation() == orientation;
    }
}
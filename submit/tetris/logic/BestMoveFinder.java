package tetris.logic;

import tetris.*;

import java.util.*;

import static java.lang.Math.*;
import static tetris.Move.*;
import static tetris.TetriminoType.*;
import static tetris.logic.EvaluationParameter.*;

public class BestMoveFinder {

    public static final ParameterWeights BEST_PARAMETERS = new ParameterWeights()
            .put(BAD_CNT, 17.064971771382382).put(HOLE_CNT, 5.733177366629449).put(HEIGHT, 1.4076593440352523).put(SEMI_BAD_CNT, 5.076315939386729).put(SCORE, -2.2862666329869157).put(HEIGHT_POW, 8.751944794972278).put(CELLS_ABOVE_TOP, 0.23822656903576234).put(FLAT_RATE, 0.6922331319044679).put(COMBO, -0.3614692532280521).put(PREV_STATE, 0.017307843006568957).put(SKIP_CNT, -7.438704615109288).put(T_SPIN_PATTERN, -12.47511154380235).put(SEMI_T_SPIN_PATTERN, -2.3599588643506335).put(LOW_EFFICIENCY, 12.290719514160582).put(MONOTONIC_RATE, 2.1585022287073583).put(I_PATTERN, -5.377388018547392)
            .put(LOW_EFFICIENCY2, 6);

    public static final int DEFAULT_ONE_MORE_LEVEL_NODES_CNT = 5;

    private final Evaluator evaluator;
    private final int oneMoreLevelNodesCnt;

    public static BestMoveFinder getBest() {
        return new BestMoveFinder(BEST_PARAMETERS, DEFAULT_ONE_MORE_LEVEL_NODES_CNT);
    }

    public BestMoveFinder(ParameterWeights parameterWeight, int oneMoreLevelNodesCnt) {
        this.oneMoreLevelNodesCnt = oneMoreLevelNodesCnt;
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
                ),
                true,
                oneMoreLevelNodesCnt != 1
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

    private MovesWithEvaluation findBestMoves(GameState2 gameState, boolean shouldFindMoves, boolean shouldUseOneMoreLevel) {
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
                    evaluator.getEvaluation(board, score, 0, prevStateEval, 0, linesCleared, true, round).evaluation
            );
        }

        List<SearchState> searchStates = new ArrayList<>();

        if (skipCnt > 0) {
            Board newBoard = board.skipMove(score, round).getBoard();
            for (int i = 0; i < possibleGarbage; i++) {
                newBoard.addPenalty();
            }
            EvaluationState curEvaluation = evaluator.getEvaluation(newBoard, score, combo, prevStateEval, skipCnt - 1, 0, false, round);

            TetriminoWithPosition nextTwp = nextTetrimino == null ? null : newBoard.newFallingTetrimino(nextTetrimino);
            GameState2 newGameState = new GameState2(
                    newBoard,
                    nextTwp,
                    null,
                    score,
                    combo,
                    round + 1,
                    curEvaluation.evaluation,
                    skipCnt - 1,
                    0,
                    linesCleared
            );

            if (nextTetrimino == null || curEvaluation.lost) {
                searchStates.add(new SearchState(curEvaluation.evaluation, newGameState, null, curEvaluation.lost));
            } else {
                double evaluation = findBestMoves(newGameState, false, false).getEvaluation();
                searchStates.add(new SearchState(evaluation, newGameState, null, curEvaluation.lost));
            }
        }

        TetriminoWithPosition[][][] bfs = PathFinder.bfs(board, fallingTetrimino);
        List<TetriminoWithPosition> availableFinalPositions = getAvailableFinalPositions(board, fallingTetrimino, bfs);

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
            TetriminoWithPosition nextTwp = nextTetrimino == null ? null : newBoard.newFallingTetrimino(nextTetrimino);
            GameState2 newGameState = new GameState2(
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
            );
            if (nextTetrimino == null || curEvaluation.lost) {
                searchStates.add(new SearchState(curEvaluation.evaluation, newGameState, finalPosition, curEvaluation.lost));
            } else {
                double evaluation = findBestMoves(newGameState, false, false).getEvaluation();
                searchStates.add(new SearchState(evaluation, newGameState, finalPosition, curEvaluation.lost));
            }
        }

        Collections.sort(searchStates);
        SearchState bestSearchState;
        if (shouldUseOneMoreLevel) {
            bestSearchState = null;
            double bestEvaluation = Double.POSITIVE_INFINITY;
            for (int i = 0; i < min(searchStates.size(), oneMoreLevelNodesCnt); i++) {
                SearchState searchState = searchStates.get(i);
                double evaluation = expectedEvaluation(searchState, nextTetrimino);
                if (bestSearchState == null || evaluation < bestEvaluation) {
                    bestSearchState = searchState;
                    bestEvaluation = evaluation;
                }
            }
        } else {
            bestSearchState = searchStates.get(0);
        }

        if (bestSearchState.position == null) { // Skip was the best move. Warning! Very ugly code!
            return new MovesWithEvaluation(Collections.singletonList(SKIP), bestSearchState.evaluation);
        }
        List<Move> moves = shouldFindMoves ? PathFinder.findMoves(fallingTetrimino, bfs, bestSearchState) : null;
        return new MovesWithEvaluation(moves, bestSearchState.evaluation);
    }

    private double expectedEvaluation(SearchState searchState, TetriminoType nextTetrimino) {
        if (searchState.lost) {
            return searchState.evaluation;
        }
        double evaluation = 0;
        for (TetriminoType type : TetriminoType.ALL) {
            GameState2 curGameState = searchState.gameState;

            TetriminoWithPosition cur;
            TetriminoType next;
            if (nextTetrimino == null) {
                cur = curGameState.board.newFallingTetrimino(type);
                next = null;
            } else {
                cur = curGameState.board.newFallingTetrimino(nextTetrimino);
                next = type;
            }
            evaluation += findBestMoves(
                    new GameState2(
                            curGameState.board,
                            cur,
                            next,
                            curGameState.score,
                            curGameState.combo,
                            curGameState.round,
                            curGameState.prevStateEval,
                            curGameState.skipCnt, curGameState.possibleGarbage,
                            curGameState.linesCleared
                    ),
                    false,
                    false).getEvaluation();
        }
        evaluation /= TetriminoType.ALL.length;
        return evaluation;
    }

    private List<TetriminoWithPosition> getAvailableFinalPositions(Board board, TetriminoWithPosition fallingTetrimino, TetriminoWithPosition[][][] bfs) {
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
        return availableFinalPositions;
    }

    static class SearchState implements Comparable<SearchState> {
        final double evaluation;
        final GameState2 gameState;
        final TetriminoWithPosition position; // null means move is skip. ugly =(
        final boolean lost;

        SearchState(double evaluation, GameState2 gameState, TetriminoWithPosition position, boolean lost) {
            this.evaluation = evaluation;
            this.gameState = gameState;
            this.position = position;
            this.lost = lost;
        }

        @Override
        public int compareTo(SearchState o) {
            return Double.compare(evaluation, o.evaluation);
        }
    }

    @SuppressWarnings("unused")
    private static boolean stopOn(TetriminoWithPosition twp, TetriminoType type, int row, int col, int orientation) {
        return twp.getTopRow() == row && twp.getLeftCol() == col && twp.getTetrimino().getType() == type && twp.getTetrimino().getOrientation() == orientation;
    }
}
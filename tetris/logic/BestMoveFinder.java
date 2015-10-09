package tetris.logic;

import tetris.*;

import java.util.ArrayList;
import java.util.List;

import static tetris.Move.DROP;
import static tetris.Move.LEFT;
import static tetris.Move.RIGHT;

public class BestMoveFinder {

    private final Evaluator evaluator = new Evaluator();

    public List<Move> findBestMoves(GameState gameState) {
        ColumnAndOrientation target = findBestMove(gameState);
        TetriminoWithPosition fallingTetrimino = gameState.getFallingTetrimino();
        Tetrimino tetrimino = fallingTetrimino.getTetrimino();

        List<Move> moves = new ArrayList<>();

        if (!tetrimino.equals(target.getTetrimino())) {
            if (tetrimino.rotateCW().equals(target.getTetrimino())) {
                fallingTetrimino = fallingTetrimino.rotateCW();
                moves.add(Move.ROTATE_CW);
            } else if (tetrimino.rotateCW().rotateCW().equals(target.getTetrimino())) {
                fallingTetrimino = fallingTetrimino.rotateCW().rotateCW();
                moves.add(Move.ROTATE_CW);
                moves.add(Move.ROTATE_CW);
            } else {
                fallingTetrimino = fallingTetrimino.rotateCCW();
                moves.add(Move.ROTATE_CCW);
            }
        }
        if (target.getColumn() > fallingTetrimino.getLeftCol()) {
            for (int i = 0; i < target.getColumn() - fallingTetrimino.getLeftCol(); i++) {
                moves.add(RIGHT);
            }
        } else if (target.getColumn() < fallingTetrimino.getLeftCol()) {
            for (int i = 0; i < fallingTetrimino.getLeftCol() - target.getColumn(); i++) {
                moves.add(LEFT);
            }
        }
        moves.add(DROP);
        return moves;
    }

    private ColumnAndOrientation findBestMove(GameState gameState) {
        TetriminoWithPosition fallingTetrimino = gameState.getFallingTetrimino();
        Action bestAction = findBestAction(gameState.getBoard(), fallingTetrimino.getTetrimino(), gameState.getNextTetrimino(), 0, gameState.getCombo()).getAction();

        if (bestAction == null) {
            return null;
        }

        Tetrimino tetrimino = fallingTetrimino.getTetrimino();
        for (int i = 0; i < bestAction.getCwRotationCnt(); i++) {
            tetrimino = tetrimino.rotateCW();
        }
        return new ColumnAndOrientation(bestAction.getNewLeftCol(), tetrimino);
    }

    private ActionWithEvaluation findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino, int score, int combo) {
        EvaluationState bestState = null;
        Action bestAction = null;

        Tetrimino originalTetrimino = fallingTetrimino;
        for (int rotateCnt = 0; rotateCnt < 4; rotateCnt++) {
            for (int newLeftCol = 0; newLeftCol + fallingTetrimino.getWidth() - 1 < board.getWidth(); newLeftCol++) {
                DropResult dropResult = board.drop(fallingTetrimino, newLeftCol);
                if (dropResult == null) {
                    continue;
                }
                Board newBoard = dropResult.getBoard();

                int scoreDelta = getScore(dropResult.getLinesCleared(), combo);
                int newScore = score + scoreDelta;
                int newCombo = dropResult.getLinesCleared() > 0 ? combo + 1 : 0;

                EvaluationState curState;

                if (nextTetrimino == null) {
                    curState = evaluator.getEvaluation(newBoard, newScore, newCombo);
                } else {
                    curState = findBestAction(newBoard, nextTetrimino, null, newScore, newCombo).getState();
                }
                if (curState != null && curState.better(bestState)) {
                    bestState = curState;
                    bestAction = new Action(newLeftCol, rotateCnt);
                }
            }
            fallingTetrimino = fallingTetrimino.rotateCW();
            if (fallingTetrimino.equals(originalTetrimino)) {
                break;
            }
        }
        return new ActionWithEvaluation(bestAction, bestState);
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
package tetris.logic;

import tetris.*;

public class BestMoveFinder {

    private final Evaluator evaluator = new Evaluator();

    public ColumnAndOrientation findBestMove(GameState gameState) {
        TetriminoWithPosition fallingTetrimino = gameState.getFallingTetrimino();
        Action bestAction = findBestAction(gameState.getBoard(), fallingTetrimino.getTetrimino(), gameState.getNextTetrimino()).getAction();

        if (bestAction == null) {
            return null;
        }

        Tetrimino tetrimino = fallingTetrimino.getTetrimino();
        for (int i = 0; i < bestAction.getCwRotationCnt(); i++) {
            tetrimino = tetrimino.rotateCW();
        }
        return new ColumnAndOrientation(bestAction.getNewLeftCol(), tetrimino);
    }

    public ActionWithEvaluation findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino) {
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

                EvaluationState curState;

                if (nextTetrimino == null) {
                    curState = evaluator.getEvaluation(newBoard);
                } else {
                    curState = findBestAction(newBoard, nextTetrimino, null).getState();
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

    public ActionWithEvaluation findBestAction(Board board, Tetrimino tetrimino) {
        return findBestAction(board, tetrimino, null);
    }
}
package tetris.logic;

import tetris.*;

public class BestMoveFinder {

    private final Evaluator evaluator = new Evaluator();

    public ColumnAndOrientation findBestMove(GameState gameState) {
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

    public ActionWithEvaluation findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino, int score, int combo) {
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
        return comboBefore;
    }

    public Action findBestAction(Board board, Tetrimino tetrimino) {
        return findBestAction(board, tetrimino, null, 0, 0).getAction();
    }


    public Action findBestAction(Board board, Tetrimino fallingTetrimino, Tetrimino nextTetrimino) {
        return findBestAction(board, fallingTetrimino, nextTetrimino, 0, 0).getAction();
    }
}
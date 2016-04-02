package tetris.logic;

import tetris.*;

import java.util.Collections;
import java.util.List;

public class PossibleGarbageCalculator {

    private final BestMoveFinder bestMoveFinder = new BestMoveFinder(ParameterWeights.zero().put(EvaluationParameter.SCORE, -1), 1);

    public List<Integer> calculatePossibleGarbage(Board board, TetriminoType tetriminoType, int prevScore, int combo) {
        TetriminoWithPosition fallingTetrimino = board.newFallingTetrimino(tetriminoType);
        if (board.collides(fallingTetrimino)) {
            return Collections.singletonList(0);
        }
        List<Move> moves = bestMoveFinder.findBestMoves(new GameState(board, fallingTetrimino, null, combo, 1, 0, Collections.singletonList(0)));
        DropResult dropResult = board.moveAndDrop(fallingTetrimino, moves, combo, 1);
        int newScore = prevScore + dropResult.getScoreAdded();
        return Collections.singletonList(Rules.calculateGarbage(prevScore, newScore));
    }
}

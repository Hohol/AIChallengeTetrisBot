package submit.tetris.logic;

import submit.tetris.Move;

import java.util.List;

public class MovesWithEvaluation {
    private final List<Move> moves;
    private final EvaluationState state;

    public MovesWithEvaluation(List<Move> moves, EvaluationState state) {
        this.moves = moves;
        this.state = state;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public EvaluationState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "ActionWithEvaluation{" +
                "moves=" + moves +
                ", state=" + state +
                '}';
    }
}
package tetris.logic;

import tetris.Move;

import java.util.List;

public class MovesWithEvaluation {
    private final List<Move> moves;
    private final double state;

    public MovesWithEvaluation(List<Move> moves, double state) {
        this.moves = moves;
        this.state = state;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public double getEvaluation() {
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
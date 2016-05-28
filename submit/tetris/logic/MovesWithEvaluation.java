package tetris.logic;

import tetris.Move;

import java.util.List;

public class MovesWithEvaluation {
    private final List<Move> moves;
    private final double evaluation;

    public MovesWithEvaluation(List<Move> moves, double evaluation) {
        this.moves = moves;
        this.evaluation = evaluation;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public double getEvaluation() {
        return evaluation;
    }

    @Override
    public String toString() {
        return "ActionWithEvaluation{" +
                "moves=" + moves +
                ", evaluation=" + evaluation +
                '}';
    }
}
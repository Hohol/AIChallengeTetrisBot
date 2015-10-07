package tetris.logic;

public class ActionWithEvaluation {
    private final Action action;
    private final EvaluationState state;

    public ActionWithEvaluation(Action action, EvaluationState state) {
        this.action = action;
        this.state = state;
    }

    public Action getAction() {
        return action;
    }

    public EvaluationState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "ActionWithEvaluation{" +
                "action=" + action +
                ", state=" + state +
                '}';
    }
}
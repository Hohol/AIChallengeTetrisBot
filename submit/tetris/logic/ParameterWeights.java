package tetris.logic;

import java.util.EnumMap;
import java.util.Map;

public class ParameterWeights {
    private final Map<EvaluationParameter, Double> map = new EnumMap<>(EvaluationParameter.class);

    ParameterWeights put(EvaluationParameter parameter, double weight) {
        map.put(parameter, weight);
        return this;
    }

    double get(EvaluationParameter parameter) {
        return map.get(parameter);
    }
}

package tetris.logic;

import java.util.EnumMap;
import java.util.Map;

public class ParameterWeights {
    private final Map<EvaluationParameter, Double> map = new EnumMap<>(EvaluationParameter.class);

    public ParameterWeights() {
    }

    public ParameterWeights(ParameterWeights parameterWeights) {
        map.putAll(parameterWeights.map);
    }

    public ParameterWeights put(EvaluationParameter parameter, double weight) {
        map.put(parameter, weight);
        return this;
    }

    public double get(EvaluationParameter parameter) {
        return map.get(parameter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (EvaluationParameter parameter : EvaluationParameter.values()) {
            sb.append(".put(").append(parameter).append(",").append(get(parameter)).append(")");
        }
        return sb.toString();
    }
}

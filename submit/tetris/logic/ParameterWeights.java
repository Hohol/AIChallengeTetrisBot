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

    public static ParameterWeights parse(String s) {
        s = s.trim();
        String[] a = s.split("\\).put\\(");
        a[0] = a[0].substring(5);
        String last = a[a.length - 1];
        a[a.length - 1] = last.substring(0, last.length() - 1);
        ParameterWeights r = new ParameterWeights();
        for (String s1 : a) {
            String[] b = s1.split(",");
            EvaluationParameter parameter = EvaluationParameter.valueOf(b[0]);
            double value = Double.parseDouble(b[1]);
            r.put(parameter, value);
        }
        return r;
    }

    public ParameterWeights zeroOut() {
        for (EvaluationParameter parameter : EvaluationParameter.values()) {
            put(parameter, 0);
        }
        return this;
    }
}

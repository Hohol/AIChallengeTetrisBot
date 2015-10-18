package genetic.tetris;

import local.MatchMaker;
import local.MatchResult;
import tetris.logic.BestMoveFinder;
import tetris.logic.EvaluationParameter;
import tetris.logic.ParameterWeights;

import java.util.*;

import static local.MatchResult.*;
import static tetris.logic.EvaluationParameter.*;
import static tetris.logic.EvaluationParameter.SCORE;

public class GeneticTetris {

    public static void main(String[] args) {
        Random rnd = new Random();
        MatchMaker matchMaker = new MatchMaker();
        List<CreatureAndWinCnt> species = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            species.add(new CreatureAndWinCnt(
                            new ParameterWeights()
//                                    .put(BAD_CNT, rnd.nextDouble())
//                                    .put(HOLE_CNT, rnd.nextDouble())
//                                    .put(HEIGHT, rnd.nextDouble())
//                                    .put(SEMI_BAD_CNT, rnd.nextDouble())
//                                    .put(SCORE, rnd.nextDouble()));
                                    .put(BAD_CNT, 0)
                                    .put(HOLE_CNT, 0)
                                    .put(HEIGHT, 0)
                                    .put(SEMI_BAD_CNT, 0)
                                    .put(SCORE, 0))
            );
            //species.add(BestMoveFinder.BEST_PARAMETERS);
        }
        int round = 0;
        while (true) {
            round++;
            System.out.println("round = " + round);
            int a = rnd.nextInt(species.size());
            int b = rnd.nextInt(species.size() - 1);
            if (b == a) {
                b++;
            }
            CreatureAndWinCnt firstPair = species.get(a);
            CreatureAndWinCnt secondPair = species.get(b);

            ParameterWeights first = firstPair.parameterWeights;
            ParameterWeights second = secondPair.parameterWeights;

            MatchResult matchResult = matchMaker.playMatch(new BestMoveFinder(first), new BestMoveFinder(second));
            if (matchResult == FIRST_WON) {
                species.set(b, new CreatureAndWinCnt(child(first, rnd)));
                firstPair.winCnt++;
            } else if (matchResult == SECOND_WON) {
                species.set(a, new CreatureAndWinCnt(child(second, rnd)));
                secondPair.winCnt++;
            }
            Collections.sort(species, Comparator.comparing(CreatureAndWinCnt::getWinCnt));
            for (CreatureAndWinCnt specy : species) {
                System.out.println("winCnt = " + specy.winCnt + " " + specy.parameterWeights);
            }
        }
    }

    /*private static ParameterWeights child(ParameterWeights parameterWeights, Random rnd) {
        int x = rnd.nextInt(6);
        ParameterWeights child = new ParameterWeights(parameterWeights);
        EvaluationParameter parameter = EvaluationParameter.values()[rnd.nextInt(EvaluationParameter.values().length)];
        if (x == 0) {
        } else if (x == 1) {
            child.put(parameter, child.get(parameter) + 0.1);
        } else if (x == 2) {
            child.put(parameter, child.get(parameter) - 0.1);
        } else if (x == 3) {
            child.put(parameter, child.get(parameter) * 0.9);
        } else if (x == 4) {
            child.put(parameter, child.get(parameter) * 1.1);
        } else if (x == 5) {
            child.put(parameter, rnd.nextDouble());
        }
        return child;
    }*/

    private static ParameterWeights child(ParameterWeights parameterWeights, Random rnd) {
        ParameterWeights child = new ParameterWeights(parameterWeights);
        EvaluationParameter parameter = EvaluationParameter.values()[rnd.nextInt(EvaluationParameter.values().length)];
        child.put(parameter, child.get(parameter) + rnd.nextGaussian());
        return child;
    }

    static class CreatureAndWinCnt {
        ParameterWeights parameterWeights;

        public int getWinCnt() {
            return winCnt;
        }

        int winCnt;

        public CreatureAndWinCnt(ParameterWeights parameterWeights) {
            this.parameterWeights = parameterWeights;
        }
    }
}

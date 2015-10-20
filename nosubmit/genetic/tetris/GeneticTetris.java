package genetic.tetris;

import local.MatchMaker;
import local.MatchResult;
import tetris.logic.BestMoveFinder;
import tetris.logic.EvaluationParameter;
import tetris.logic.ParameterWeights;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static local.MatchResult.*;

public class GeneticTetris {

    public static final int GAMES_CNT = 1;

    public static void main(String[] args) {
        Random rnd = new Random();

        AtomicReference<ParameterWeights> currentBestRef = new AtomicReference<>();
        MatchMaker matchMaker = new MatchMaker();
        List<CreatureAndWinCnt> species = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            species.add(new CreatureAndWinCnt(
                    //randomParameters(rnd)
                    zeroParameters()
            ));
            //species.add(BestMoveFinder.BEST_PARAMETERS);
        }

        // todo remove
        species.get(0).parameterWeights.put(EvaluationParameter.BAD_CNT, 1); // todo remove

        new Thread(new ResultsLogger(currentBestRef)).start();
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

            MatchResult matchResult = getMatchSeriesResult(matchMaker, first, second);
            if (matchResult == FIRST_WON) {
                species.set(b, new CreatureAndWinCnt(child(first, rnd)));
                firstPair.winCnt++;
            } else if (matchResult == SECOND_WON) {
                species.set(a, new CreatureAndWinCnt(child(second, rnd)));
                secondPair.winCnt++;
            }
            Collections.sort(species, Comparator.comparing(CreatureAndWinCnt::getWinCnt));
            synchronized (currentBestRef) {
                currentBestRef.set(species.get(species.size() - 1).parameterWeights);
            }
            for (CreatureAndWinCnt specy : species) {
                System.out.println("winCnt = " + specy.winCnt + " " + specy.parameterWeights);
            }
        }
    }

    private static MatchResult getMatchSeriesResult(MatchMaker matchMaker, ParameterWeights first, ParameterWeights second) {
        int pts = 0; // 0 for loss, 1 for draw, 2 for win
        for (int i = 0; i < GAMES_CNT; i++) {
            MatchResult matchResult = matchMaker.playMatch(new BestMoveFinder(first), new BestMoveFinder(second));
            if (matchResult == FIRST_WON) {
                pts += 2;
            } else if (matchResult == DRAW) {
                pts += 1;
            }
        }
        if (pts == GAMES_CNT) {
            return DRAW;
        } else if (pts < GAMES_CNT) {
            return SECOND_WON;
        } else {
            return FIRST_WON;
        }
    }

    private static ParameterWeights randomParameters(Random rnd) {
        ParameterWeights parameterWeights = new ParameterWeights();
        for (EvaluationParameter parameter : EvaluationParameter.values()) {
            parameterWeights.put(parameter, rnd.nextDouble());
        }
        return parameterWeights;
    }

    private static ParameterWeights zeroParameters() {
        ParameterWeights parameterWeights = new ParameterWeights();
        for (EvaluationParameter parameter : EvaluationParameter.values()) {
            parameterWeights.put(parameter, 0);
        }
        return parameterWeights;
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

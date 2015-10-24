package local;

import tetris.logic.BestMoveFinder;
import tetris.logic.ParameterWeights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static tetris.logic.EvaluationParameter.*;

public class RoundRobin {

    public static void main(String[] args) {
        MatchMaker matchMaker = new MatchMaker();
        List<PlayerEntry> entries = new ArrayList<>();

        ParameterWeights parameters = new ParameterWeights()
                .put(BAD_CNT, 5.582919899887908).put(HOLE_CNT, 2.878471664579383).put(HEIGHT, 1.4631383737117991).put(SEMI_BAD_CNT, 2.133187913129006).put(SCORE, -0.716393996010999).put(HEIGHT_POW, 4.672254358240745).put(CELLS_ABOVE_TOP, 0.8587277875132031).put(FLAT_RATE, 0.733147384274665).put(COMBO, -0.16547251812410724)
                .put(PREV_STATE, 0);
        for (double p = 0; p <= 0.2; p += 0.1) {
            addInitial(
                    entries,
                    new ParameterWeights(
                            new ParameterWeights(parameters).put(PREV_STATE, p)
                    )
            );
        }

        int round = 0;
        while (true) {
            round++;
            for (int a = 0; a < entries.size(); a++) {
                for (int b = a + 1; b < entries.size(); b++) {

                    PlayerEntry firstEntry = entries.get(a);
                    PlayerEntry secondEntry = entries.get(b);

                    ParameterWeights first = firstEntry.parameters;
                    ParameterWeights second = secondEntry.parameters;

                    MatchResult matchResult = matchMaker.playMatch(new BestMoveFinder(first), new BestMoveFinder(second));
                    update(firstEntry, secondEntry, matchResult, round);

                    Collections.sort(entries, Comparator.comparing(PlayerEntry::getWinRate));
                    for (PlayerEntry entry : entries) {
                        System.out.println(entry);
                        System.out.println();
                    }
                    System.out.println("end of round = " + round);
                    System.out.println("---------------------------------------------------------");
                }
            }
        }
    }

    private static void addInitial(List<PlayerEntry> entries, ParameterWeights parameters) {
        entries.add(
                new PlayerEntry(new ParameterWeights(
                        parameters
                ))
        );
    }

    private static void update(PlayerEntry firstEntry, PlayerEntry secondEntry, MatchResult matchResult, int round) {
        firstEntry.gameCnt++;
        secondEntry.gameCnt++;
        switch (matchResult) {
            case FIRST_WON:
                firstEntry.pts += 2;
                break;
            case SECOND_WON:
                secondEntry.pts += 2;
                break;
            case DRAW:
                firstEntry.pts += 1;
                secondEntry.pts += 1;
                break;
            default:
                throw new RuntimeException();
        }
    }

    static class PlayerEntry {
        final ParameterWeights parameters;
        int pts;
        int gameCnt;

        PlayerEntry(ParameterWeights parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "winRate = " + getWinRate() + "\n"
                    + "gameCnt = " + gameCnt + "\n"
                    + parameters;
        }

        private double getWinRate() {
            if (gameCnt == 0) {
                return 0;
            }
            return pts / (gameCnt * 2.0);
        }
    }
}

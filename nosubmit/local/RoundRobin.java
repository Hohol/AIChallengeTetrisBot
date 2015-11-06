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

        ParameterWeights parameters = BestMoveFinder.BEST_PARAMETERS;
        int steps = 3;
        for (int flatRate = 0; flatRate < steps; flatRate++) {
            for (int monotonicRate = 0; monotonicRate < steps; monotonicRate++) {
                addInitial(
                        entries,
                        new ParameterWeights(parameters)
                                .put(FLAT_RATE, flatRate * 1.0 / steps)
                                .put(MONOTONIC_RATE, monotonicRate * 1.0 / steps)
                );
            }
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

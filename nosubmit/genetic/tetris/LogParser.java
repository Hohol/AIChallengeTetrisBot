package genetic.tetris;

import local.MatchMaker;
import local.MatchResult;
import tetris.logic.BestMoveFinder;
import tetris.logic.ParameterWeights;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static local.MatchResult.DRAW;
import static local.MatchResult.SECOND_WON;

public class LogParser {
    public static final int GAMES_CNT = 1000;

    public static void main(String[] args) throws FileNotFoundException {
        Locale.setDefault(Locale.US);
        Scanner in = new Scanner(new FileInputStream("log2.txt"));
        List<WinRateAndParameters> entries = new ArrayList<>();
        while (in.hasNext()) {
            String s = in.next();
            if (s.equals("New")) {
                in.next();
                in.next();
            }
            ParameterWeights weights = ParameterWeights.parse(in.next());
            in.next();
            in.next();
            double winRate = in.nextDouble();
            entries.add(new WinRateAndParameters(winRate, weights));
        }
        Collections.sort(entries, Comparator.comparing(WinRateAndParameters::getWinRate).reversed());
        int bestPts = -1;
        MatchMaker matchMaker = new MatchMaker();
        for (WinRateAndParameters entry : entries) {
            ParameterWeights currentBest = entry.weights;
            System.out.println("params: " + currentBest);
            System.out.println("winRate out of 100: " + entry.winRate);
            int pts = 0; // 0 for loss, 1 for draw, 2 for win
            for (int i = 0; i < GAMES_CNT; i++) {
                MatchResult matchResult = matchMaker.playMatch(BestMoveFinder.getBest(), new BestMoveFinder(currentBest));
                if (matchResult == SECOND_WON) {
                    pts += 2;
                } else if (matchResult == DRAW) {
                    pts += 1;
                }
            }
            double winRate = pts / (GAMES_CNT * 2.0);
            System.out.println("winRate = " + winRate);
            if (pts > bestPts) {
                bestPts = pts;
                System.out.println("New record!");
            }
            System.out.println();
            System.out.flush();
        }
    }

    static class WinRateAndParameters {
        final double winRate;
        final ParameterWeights weights;

        WinRateAndParameters(double winRate, ParameterWeights weights) {
            this.winRate = winRate;
            this.weights = weights;
        }

        public double getWinRate() {
            return winRate;
        }
    }
}

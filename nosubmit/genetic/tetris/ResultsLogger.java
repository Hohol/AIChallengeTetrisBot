package genetic.tetris;

import local.MatchMaker;
import local.MatchResult;
import tetris.logic.BestMoveFinder;
import tetris.logic.ParameterWeights;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

import static local.MatchResult.*;

public class ResultsLogger implements Runnable {
    private final AtomicReference<ParameterWeights> currentBestRef;
    public static final int GAMES_CNT = 100;

    public ResultsLogger(AtomicReference<ParameterWeights> currentBestRef) {
        this.currentBestRef = currentBestRef;
    }

    @Override
    public void run() {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileOutputStream("log.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        MatchMaker matchMaker = new MatchMaker();
        int bestPts = -1;
        while (true) {
            ParameterWeights currentBest;
            synchronized (currentBestRef) {
                currentBest = currentBestRef.get();
            }
            if (currentBest == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            int pts = 0; // 0 for loss, 1 for draw, 2 for win
            for (int i = 0; i < GAMES_CNT; i++) {
                MatchResult matchResult = matchMaker.playMatch(BestMoveFinder.getBest(), new BestMoveFinder(currentBest, BestMoveFinder.DEFAULT_ONE_MORE_LEVEL_NODES_CNT));
                if (matchResult == SECOND_WON) {
                    pts += 2;
                } else if (matchResult == DRAW) {
                    pts += 1;
                }
            }
            double winRate = pts / (GAMES_CNT * 2.0);
            out.println("params: " + currentBest);
            out.println("winRate = " + winRate);
            if (pts > bestPts) {
                bestPts = pts;
                out.println("New record!");
            }
            out.println();
            out.flush();
        }
    }
}

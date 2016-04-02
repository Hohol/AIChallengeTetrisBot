package local;

import tetris.Board;
import tetris.Holes;
import tetris.TetriminoType;
import tetris.logic.BestMoveFinder;
import tetris.logic.PossibleGarbageCalculator;

import java.util.List;
import java.util.Random;

import static local.MatchResult.*;

public class MatchMaker {

    public static void main(String[] args) {
        BestMoveFinder first = new BestMoveFinder(
                BestMoveFinder.BEST_PARAMETERS
                ,
                5
        );
        BestMoveFinder second = new BestMoveFinder(
                BestMoveFinder.BEST_PARAMETERS
                ,
                10
        );
        int matchCnt = 0;
        int[] resultToCnt = new int[3];
        while (true) {
            MatchResult matchResult = new MatchMaker().playMatch(first, second);
            matchCnt++;
            resultToCnt[matchResult.ordinal()]++;
            System.out.println("matchCnt = " + matchCnt);
            for (int i = 0; i < 3; i++) {
                System.out.println(MatchResult.values()[i] + " " + (resultToCnt[i] / (double) matchCnt));
            }
            System.out.println();
        }
    }

    public MatchResult playMatch(BestMoveFinder firstPlayer, BestMoveFinder secondPlayer) {
        Random rnd = new Random();

        FullGameState firstGameState = new FullGameState(new Board(Board.STANDARD_HEIGHT, Board.STANDARD_WIDTH));
        FullGameState secondGameState = new FullGameState(new Board(Board.STANDARD_HEIGHT, Board.STANDARD_WIDTH));
        TetriminoType curTetrimino = getRandomTetrimino(rnd);
        TetriminoType nextTetrimino = getRandomTetrimino(rnd);
        PossibleGarbageCalculator possibleGarbageCalculator = new PossibleGarbageCalculator();

        while (true) {
            log("Round = " + firstGameState.round);
            List<Integer> possibleGarbageToFirst = possibleGarbageCalculator.calculatePossibleGarbage(
                    secondGameState.board,
                    curTetrimino,
                    secondGameState.score,
                    secondGameState.combo
            );
            List<Integer> possibleGarbageToSecond = possibleGarbageCalculator.calculatePossibleGarbage(
                    firstGameState.board,
                    curTetrimino,
                    firstGameState.score,
                    firstGameState.combo
            );
            firstGameState.makeMove(
                    curTetrimino,
                    nextTetrimino,
                    possibleGarbageToFirst,
                    firstPlayer
            );
            long start = System.currentTimeMillis();
            secondGameState.makeMove(
                    curTetrimino,
                    nextTetrimino,
                    possibleGarbageToSecond,
                    secondPlayer
            );
            System.out.println(System.currentTimeMillis() - start);

            if (firstGameState.lost && secondGameState.lost) {
                return DRAW;
            }
            if (firstGameState.lost) {
                return SECOND_WON;
            }
            if (secondGameState.lost) {
                return FIRST_WON;
            }

            generateGarbage(firstGameState, rnd, secondGameState);
            generateGarbage(secondGameState, rnd, firstGameState);

            log("cur = " + curTetrimino);
            log("next = " + nextTetrimino);
            //log(firstGameState.board);
            log("lines sent to second: " + firstGameState.garbageSentOnLastMove);
            log("");
            log(secondGameState.board);
            log("lines sent to first: " + secondGameState.garbageSentOnLastMove);

            if (firstGameState.lost && secondGameState.lost) {
                return DRAW;
            }
            if (firstGameState.lost) {
                return SECOND_WON;
            }
            if (secondGameState.lost) {
                return FIRST_WON;
            }
            log("");

            curTetrimino = nextTetrimino;
            nextTetrimino = getRandomTetrimino(rnd);
        }
    }

    private void log(Object o) {
        //System.out.println(o);
    }

    private void generateGarbage(FullGameState state, Random rnd, FullGameState opponent) {
        Holes[] holes = new Holes[opponent.garbageSentOnLastMove];
        boolean twoHoles = (state.garbageAcceptedSum % 2 == 0);
        for (int i = 0; i < opponent.garbageSentOnLastMove; i++) {
            int a = rnd.nextInt(Board.STANDARD_WIDTH);
            int b;
            if (twoHoles) {
                b = rnd.nextInt(Board.STANDARD_WIDTH - 1);
                if (b >= a) {
                    b++;
                }
            } else {
                b = -1;
            }
            holes[i] = new Holes(a, b);
            twoHoles = !twoHoles;
        }
        state.addGarbage(holes);
    }

    private static TetriminoType getRandomTetrimino(Random rnd) {
        return TetriminoType.values()[rnd.nextInt(TetriminoType.values().length)];
    }
}

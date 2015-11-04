package local;

import tetris.Board;
import tetris.Holes;
import tetris.TetriminoType;
import tetris.logic.BestMoveFinder;
import tetris.logic.ParameterWeights;

import java.util.Random;

import static local.MatchResult.*;
import static tetris.logic.EvaluationParameter.*;

public class MatchMaker {

    public static void main(String[] args) {
        BestMoveFinder first = BestMoveFinder.getBest();
        BestMoveFinder second = new BestMoveFinder(new ParameterWeights()
                .put(BAD_CNT,6.68473349725179).put(HOLE_CNT,2.878471664579383).put(HEIGHT,1.4631383737117991).put(SEMI_BAD_CNT,2.133187913129006).put(SCORE,-0.716393996010999).put(HEIGHT_POW,4.672254358240745).put(CELLS_ABOVE_TOP,0.05887754151955771).put(FLAT_RATE, 0.733147384274665).put(COMBO,-0.16547251812410724).put(PREV_STATE,0.1518503463877854).put(SKIP_CNT,-5.079457554219985).put(T_SPIN_PATTERN,-8.598925331599782).put(SEMI_T_SPIN_PATTERN,-0.1992487707283843).put(LOW_EFFICIENCY,1.9972101046494506)
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

        while (true) {
            log("Round = " + firstGameState.round);
            firstGameState.makeMove(curTetrimino, nextTetrimino, firstPlayer);
            secondGameState.makeMove(curTetrimino, nextTetrimino, secondPlayer);

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
            log(firstGameState.board);
            log("lines sent to second: " + firstGameState.garbageSentOnLastMove);
            log("");
            //log(secondGameState.board);
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

package local;

import tetris.Board;
import tetris.Holes;
import tetris.TetriminoType;
import tetris.logic.BestMoveFinder;
import tetris.logic.ParameterWeights;
import tetris.logic.PossibleGarbageCalculator;

import java.util.Random;

import static local.MatchResult.*;
import static tetris.logic.EvaluationParameter.*;

public class MatchMaker {

    public static void main(String[] args) {
        BestMoveFinder first = BestMoveFinder.getBest();
        BestMoveFinder second = new BestMoveFinder(new ParameterWeights()
                .put(BAD_CNT,4.04244799470303).put(HOLE_CNT,2.224387958782543).put(HEIGHT,3.2032807202434537).put(SEMI_BAD_CNT,2.836906632838518).put(SCORE,-0.9228753064157523).put(HEIGHT_POW,0.0760321369445548).put(CELLS_ABOVE_TOP,0.10197293352695808).put(FLAT_RATE,0.5225395672967282).put(COMBO,-0.1).put(PREV_STATE,0.5943002519764304).put(SKIP_CNT,-2.5071420556737536).put(T_SPIN_PATTERN,-4.8708535119578205).put(SEMI_T_SPIN_PATTERN,-0.1363560523225238).put(LOW_EFFICIENCY,6.450545991594334)
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
            int possibleGarbageToFirst = possibleGarbageCalculator.calculatePossibleGarbage(
                    secondGameState.board,
                    curTetrimino,
                    secondGameState.score,
                    secondGameState.combo
            );
            int possibleGarbageToSecond = possibleGarbageCalculator.calculatePossibleGarbage(
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
            secondGameState.makeMove(
                    curTetrimino,
                    nextTetrimino,
                    possibleGarbageToSecond,
                    secondPlayer
            );

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

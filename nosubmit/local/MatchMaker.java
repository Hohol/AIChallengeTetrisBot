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
                .put(BAD_CNT,17.064971771382382).put(HOLE_CNT,5.733177366629449).put(HEIGHT,1.4076593440352523).put(SEMI_BAD_CNT,5.076315939386729).put(SCORE,-2.2862666329869157).put(HEIGHT_POW,8.751944794972278).put(CELLS_ABOVE_TOP,0.23822656903576234).put(FLAT_RATE,0.6922331319044679).put(COMBO,-0.3614692532280521).put(PREV_STATE,0.017307843006568957).put(SKIP_CNT,-7.438704615109288).put(T_SPIN_PATTERN,-12.47511154380235).put(SEMI_T_SPIN_PATTERN,-2.3599588643506335).put(LOW_EFFICIENCY,12.290719514160582).put(MONOTONIC_RATE,2.1585022287073583).put(I_PATTERN, -5.377388018547392)
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

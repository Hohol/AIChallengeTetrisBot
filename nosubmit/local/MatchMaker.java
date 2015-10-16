package local;

import tetris.Board;
import tetris.TetriminoType;
import tetris.logic.BestMoveFinder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import static local.MatchResult.*;

public class MatchMaker {

    public static void main(String[] args) {
        BestMoveFinder first = new BestMoveFinder();
        BestMoveFinder second = new BestMoveFinder();
        int matchCnt = 0;
        int[] resultToCnt = new int[3];
        while (true) {
            MatchResult matchResult = new MatchMaker().playMatch(first, second);
            matchCnt++;
            resultToCnt[matchResult.ordinal()]++;
            System.out.println(matchResult);
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

            generateGarbage(firstGameState, rnd, secondGameState.garbageSentOnLastMove);
            generateGarbage(secondGameState, rnd, firstGameState.garbageSentOnLastMove);

            log("cur = " + curTetrimino);
            log("next = " + nextTetrimino);
            log(firstGameState.board);
            log("lines sent: " + firstGameState.garbageSentOnLastMove);
            log("\n");
            log(secondGameState.board);
            log("lines sent: " + secondGameState.garbageSentOnLastMove);
            log("\n");

            if (firstGameState.lost && secondGameState.lost) {
                return DRAW;
            }
            if (firstGameState.lost) {
                return SECOND_WON;
            }
            if (secondGameState.lost) {
                return FIRST_WON;
            }

            curTetrimino = nextTetrimino;
            nextTetrimino = getRandomTetrimino(rnd);
        }
    }

    private void log(Object o) {
        //System.out.println(o);
    }

    private void generateGarbage(FullGameState state, Random rnd, int garbageSentOnLastMove) {
        List<Integer> emptyCols = new ArrayList<>();
        while (emptyCols.size() != garbageSentOnLastMove) {
            int col = rnd.nextInt(state.board.getWidth());
            if (emptyCols.contains(col)) {
                continue;
            }
            emptyCols.add(col);
        }
        int[] ar = new int[emptyCols.size()];
        for (int i = 0; i < emptyCols.size(); i++) {
            ar[i] = emptyCols.get(i);
        }
        state.addGarbage(ar);
    }

    private static TetriminoType getRandomTetrimino(Random rnd) {
        return TetriminoType.values()[rnd.nextInt(TetriminoType.values().length)];
    }
}

package tetris.logic;

import tetris.Board;

import java.util.List;

public class Evaluator {
    public EvaluationState getEvaluation(Board board, int score, int combo) {
        int badCnt = 0;
        int w = board.getWidth();

        int topBadRow = -1;
        int topBadCol = -1;

        for (int col = 0; col < w; col++) {
            boolean found = false;
            for (int row = 0; row < board.getHeight(); row++) {
                if (board.get(row, col)) {
                    found = true;
                } else {
                    if (found) {
                        badCnt++;
                        if (topBadRow == -1 || row < topBadRow) {
                            topBadRow = row;
                            topBadCol = col;
                        }
                    }
                }
            }
        }

        int cellsAboveTopBad = 0;
        if (topBadRow != -1) {
            for (int row = topBadRow - 1; row >= 0; row--) {
                if (board.get(row, topBadCol)) {
                    cellsAboveTopBad++;
                } else {
                    break;
                }
            }
        }

        int flatRate = 0;
        for (int i = 0; i < w - 1; i++) {
            int diff = Math.abs(board.getTopRowInColumn(i) - board.getTopRowInColumn(i + 1));
            flatRate += diff;
        }
        int holeCnt = 0;
        for (int i = 0; i < w; i++) {
            int left = i == 0 ? 999 : board.getColumnHeight(i - 1);
            int mid = board.getColumnHeight(i);
            int right = i == board.getWidth() - 1 ? 999 : board.getColumnHeight(i + 1);
            if (mid < Math.min(left, right) - 2) {
                holeCnt++;
            }
        }
        int maxColumnHeight = 0;
        for (int i = 0; i < w; i++) {
            maxColumnHeight = Math.max(maxColumnHeight, board.getColumnHeight(i));
        }
        boolean higherHalfHeight = maxColumnHeight > board.getHeight() / 2;
        return new EvaluationState(
                badCnt,
                flatRate,
                holeCnt,
                maxColumnHeight > board.getHeight() - 4,
                maxColumnHeight,
                score,
                combo,
                cellsAboveTopBad,
                higherHalfHeight
        );
    }
}
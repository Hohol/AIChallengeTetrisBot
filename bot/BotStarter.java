// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import java.util.ArrayList;
import java.util.Collections;

import field.CellType;
import field.Field;
import field.ShapeType;
import moves.MoveType;
import tetris.*;
import tetris.logic.BestMoveFinder;

import static tetris.Move.*;
import static tetris.Move.DROP;

/**
 * BotStarter class
 * <p>
 * This class is where the main logic should be. Implement getMoves() to
 * return something better than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {

    private final BestMoveFinder bestMoveFinder = new BestMoveFinder(1);

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

    public ArrayList<MoveType> getMoves(BotState state, long timeout) {
        ArrayList<MoveType> moves = new ArrayList<>();

        GameState gameState = getGameState(state);

        ColumnAndOrientation target = bestMoveFinder.findBestMove(gameState, false);// todo remove stash logic

        TetriminoWithPosition fallingTetrimino = new TetriminoWithPosition(
                state.getShapeLocation().y,
                state.getShapeLocation().x,
                convertTetrimino(state.getCurrentShape())
        );

        Tetrimino tetrimino = fallingTetrimino.getTetrimino();

        int column = fallingTetrimino.getLeftCol(); // todo may improve rotation logic to decrease number of moves

        if (!tetrimino.equals(target.getTetrimino())) {
            if (tetrimino.rotateCW().equals(target.getTetrimino())) {
                column = rotateCW(fallingTetrimino);
                moves.add(convertMove(Move.ROTATE_CW));
            } else if (tetrimino.rotateCW().rotateCW().equals(target.getTetrimino())) {
                column = rotateCW2(fallingTetrimino);
                moves.add(convertMove(Move.ROTATE_CW));
                moves.add(convertMove(Move.ROTATE_CW));
            } else {
                column = rotateCCW(fallingTetrimino);
                moves.add(convertMove(Move.ROTATE_CCW));
            }
        }
        if (target.getColumn() > column) {
            for (int i = 0; i < target.getColumn() - column; i++) {
                moves.add(convertMove(RIGHT));
            }
        } else if (target.getColumn() < column) {
            for (int i = 0; i < column - target.getColumn(); i++) {
                moves.add(convertMove(LEFT));
            }
        }
        moves.add(convertMove(DROP));

        return moves;
    }

    private int rotateCW(TetriminoWithPosition twp) {
        if (twp.getTetrimino().getWidth() == 4) {
            return twp.getLeftCol() + 2;
        }
        return twp.getLeftCol() + 1;
    }

    private int rotateCW2(TetriminoWithPosition twp) {
        return twp.getLeftCol();
    }

    private int rotateCCW(TetriminoWithPosition twp) {
        return twp.getLeftCol();
    }

    private MoveType convertMove(Move move) {
        switch (move) {
            case LEFT:
                return MoveType.LEFT;
            case RIGHT:
                return MoveType.RIGHT;
            case DROP:
                return MoveType.DROP;
            case ROTATE_CW:
                return MoveType.TURNRIGHT;
            case ROTATE_CCW:
                return MoveType.TURNLEFT;
            default:
                throw new RuntimeException();
        }
    }

    private GameState getGameState(BotState state) {
        Field field = state.getMyField();
        Board board = new Board(field.getWidth(), field.getHeight());
        for (int i = 0; i < field.getHeight(); i++) {
            for (int j = 0; j < field.getWidth(); j++) {
                CellType cellType = field.getCell(j, i).getState();
                board.set(i, j, cellType == CellType.SOLID || cellType == CellType.BLOCK);
                if (cellType == CellType.SOLID && board.getPenalty() == 0) {
                    board.setPenalty(board.getHeight() - i);
                }
            }
        }
        TetriminoWithPosition fallingTetrimino = new TetriminoWithPosition(
                state.getShapeLocation().y,
                state.getShapeLocation().x,
                convertTetrimino(state.getCurrentShape())
        );
        return new GameState(
                board,
                fallingTetrimino,
                Collections.singletonList(convertTetrimino(state.getNextShape())),
                null
        );
    }

    private Tetrimino convertTetrimino(ShapeType shape) {
        switch (shape) {
            case I:
                return Tetrimino.I;
            case J:
                return Tetrimino.J;
            case L:
                return Tetrimino.L;
            case O:
                return Tetrimino.O;
            case S:
                return Tetrimino.S;
            case T:
                return Tetrimino.T;
            case Z:
                return Tetrimino.Z;
            default:
                throw new RuntimeException("None tetrimino?");
        }
    }
}

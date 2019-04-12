/**
 * MIT License
 *
 * Copyright (c) 2019 Hycom S.A.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.hycom.model;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;
import pl.hycom.exception.ActionException;
import pl.hycom.exception.BombException;
import pl.hycom.exception.GameConfigurationException;

/**
 * Class responsible for create and filled board game with the appropriate values.
 *
 * @author Rafal Ceglinski
 */
@Slf4j
public class Board {

    private BoardPoint[][] boardPoints;

    private final LocalTime localDateTime = LocalTime.now();

    private State state = State.ACTIVE;

    private Board() {
    }

    public void updateStateIfGameExpired(final int timeAfterSessionExpires) {
        if (SECONDS.between(localDateTime, LocalDateTime.now()) >= timeAfterSessionExpires) {
            state = State.FAIL;
        }
    }

    public void check(final int row, final int column) throws ActionException {
        checkRowAndColumn(row, column);

        checkBomb(row, column);

        disableFields(row, column);

        checkWin();
    }

    private void checkRowAndColumn(final int row, final int column) throws ActionException {
        if (row < 0 || column < 0) {
            state = State.FAIL;
            throw new ActionException("Row and column should be higher than zero.");
        }
    }

    private void checkBomb(final int row, final int column) throws BombException {
        if (boardPoints[row][column].hasBomb()) {
            state = State.FAIL;
            throw new BombException("explosion!");
        }
    }

    private void disableFields(final int row, final int column) {
        if (!boardPoints[row][column].isAvailable()) {
            return;
        }

        boardPoints[row][column].disable();

        if (log.isInfoEnabled()) {
            log.info("Disabling field [" + row + ":" + column + "]");
        }

        disableFieldsAround(row, column);
    }

    private void disableFieldsAround(final int row, final int column) {
        if (boardPoints[row][column].getBombsAround() != 0) {
            return;
        }

        for (int i = row - 1; i <= row + 1; i++) {
            if (i >= 0 && i < boardPoints.length) {
                for (int j = column - 1; j <= column + 1; j++) {
                    if (j >= 0 && j < boardPoints.length) {
                        if (i != row || j != column) {
                            if (boardPoints[i][j].isAvailable()) {
                                boardPoints[i][j].disable();

                                log.info("Disabled field =[" + i + ":" + j + "] number of bomb =[" + boardPoints[i][j].getBombsAround() + "]");
                                disableFieldsAround(i, j);
                            }
                        }
                    }
                }
            }
        }

    }

    private void checkWin() {
        if (countAvailableFields() == countFieldsWithBomb()) {
            state = State.WIN;
        }
    }

    private long countAvailableFields() {
        long openCount = Arrays.stream(boardPoints).flatMap(Arrays::stream).filter(BoardPoint::isAvailable).count();

        if (log.isInfoEnabled()) {
            log.info("Open count [" + openCount + "]");
        }

        return openCount;
    }

    private long countFieldsWithBomb() {
        long bombsCount = Arrays.stream(boardPoints).flatMap(Arrays::stream).filter(BoardPoint::hasBomb).count();

        if (log.isInfoEnabled()) {
            log.info("Bombs count [" + bombsCount + "]");
        }

        return bombsCount;
    }

    public State state() {
        return state;
    }

    /**
     * The method is responsible for convert array to map structure.
     * It's the way how to easier manipulate of values (how many bombs is near point and which fields are disabled to choose)
     * and way how to dynamically show board game in HTML.
     */
    public Map<Integer, List<FieldDTO>> prepareDTO() {

        // the key is the row, the column is an index in the list.
        Map<Integer, List<FieldDTO>> boardContainer = new LinkedHashMap<>();

        for (int row = 0; row < boardPoints.length; row++) {
            List<FieldDTO> fields = new ArrayList<>();
            for (int column = 0; column < boardPoints.length; column++) {
                fields.add(new FieldDTO(boardPoints[row][column].getBombsAround(), !boardPoints[row][column].isAvailable(), boardPoints[row][column].hasBomb()));
            }
            boardContainer.put(row, fields);
        }

        return boardContainer;
    }

    public static class BoardFactory {

        private static final Random RANDOM = new Random();

        private BoardFactory() {
        }

        public static Board createEmpty() {
            Board board = new Board();
            prepareBoard(board, 0);
            return board;
        }

        public static Board createStatic(final File file) throws GameConfigurationException {
            Board board = new Board();

            fileValidator(board, file);
            prepareBoard(board, 8);
            addBombs(board, file);
            putNumberOfBombsAroundPoint(board);

            return board;
        }

        public static Board createRandom(final int numberBombs, final int boardSize) throws GameConfigurationException {
            Board board = new Board();

            inputParameterValidation(board, numberBombs, boardSize);
            prepareBoard(board, boardSize);
            addBombs(board, numberBombs);
            putNumberOfBombsAroundPoint(board);

            return board;
        }

        private static void fileValidator(final Board board, final File file) throws GameConfigurationException {
            if (file == null || file.length() == 0) {
                board.state = State.FAIL;
                throw new GameConfigurationException("File is null");
            }
        }

        private static void inputParameterValidation(final Board board, final int numberBombs, final int boardSize) throws GameConfigurationException {
            if (numberBombs > boardSize * boardSize) {
                board.state = State.FAIL;
                throw new GameConfigurationException("Number of bombs can't be higher than board size.");

            }

            if (numberBombs < 0 || boardSize <= 0) {
                board.state = State.FAIL;
                throw new GameConfigurationException("Number of bombs and board size can't be less than zero.");
            }
        }

        private static void prepareBoard(final Board board, final int boardSize) {
            board.boardPoints = new BoardPoint[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    board.boardPoints[i][j] = new BoardPoint();
                }
            }
        }

        private static void addBombs(final Board board, final File file) {
            try (Scanner scanner = new Scanner(file)) {
                int row = 0;
                int column = 0;
                while (scanner.hasNext()) {

                    String line = scanner.next();

                    if (Integer.valueOf(line) == 1) {
                        board.boardPoints[row][column].setBomb();
                    }

                    if (column == 7) {
                        row++;
                        column = 0;
                    } else {
                        column++;
                    }
                }

            } catch (IOException e) {
                log.error("Problem with file.");
            }
        }

        private static void addBombs(final Board board, int numberBombs) {
            while (numberBombs > 0) {
                int row = random(board.boardPoints.length);
                int column = random(board.boardPoints.length);

                if (canAddBomb(board, row, column)) {
                    board.boardPoints[row][column].setBomb();
                    numberBombs--;
                }
            }
        }

        private static int random(final int max) {
            return RANDOM.nextInt(max);
        }

        private static boolean canAddBomb(final Board board, final int row, final int column) {
            if (board.boardPoints[row][column].hasBomb()) {
                return false;
            }

            if (!bombsCheck(board, row, column)) {
                return false;
            }

            for (int i = row - 1; i <= row + 1; i++) {
                if (i >= 0 && i < board.boardPoints.length) {
                    for (int j = column - 1; j <= column + 1; j++) {
                        if (j >= 0 && j < board.boardPoints.length) {
                            if (i != row || j != column) {
                                if (!bombsCheck(board, i, j)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }

        private static boolean bombsCheck(final Board board, final int row, final int column) {
            if (countBombsAroundPoint(board, row, column) == 8) {
                return false;
            }

            if ((row == 0 || row == board.boardPoints.length - 1) && (column == 0 || column == board.boardPoints.length - 1)) {
                if (countBombsAroundPoint(board, row, column) == 3) {
                    return false;
                }
            }

            if (row == 0 || row == board.boardPoints.length - 1 || column == 0 || column == board.boardPoints.length - 1) {
                if (countBombsAroundPoint(board, row, column) == 5) {
                    return false;
                }
            }

            return true;
        }

        private static int countBombsAroundPoint(final Board board, final int row, final int column) {
            int bombsCount = 0;

            for (int i = row - 1; i <= row + 1; i++) {
                if (i >= 0 && i < board.boardPoints.length) {
                    for (int j = column - 1; j <= column + 1; j++) {
                        if (j >= 0 && j < board.boardPoints.length) {
                            if (i != row || j != column) {
                                if (board.boardPoints[i][j].hasBomb()) {
                                    bombsCount++;
                                }
                            }
                        }
                    }
                }
            }

            return bombsCount;
        }

        private static void putNumberOfBombsAroundPoint(final Board board) {
            for (int i = 0; i < board.boardPoints.length; i++) {
                for (int j = 0; j < board.boardPoints.length; j++) {
                    if (!board.boardPoints[i][j].hasBomb()) {
                        board.boardPoints[i][j].setBombsAround(countBombsAroundPoint(board, i, j));
                    }
                }
            }
        }
    }
}

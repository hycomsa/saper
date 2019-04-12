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
package pl.hycom.service;

import java.io.File;

import org.springframework.stereotype.Service;

import pl.hycom.exception.ActionException;
import pl.hycom.exception.GameConfigurationException;
import pl.hycom.model.Board;
import pl.hycom.model.Board.BoardFactory;
import pl.hycom.model.Level;

@Service
public class BoardService {

    public Board createBoard(final File file) throws GameConfigurationException {
        return BoardFactory.createStatic(file);
    }

    public Board createBoard(final Level level) throws GameConfigurationException {
        if (level == Level.HARD) {
            return BoardFactory.createRandom(13, 8);
        }

        if (level == Level.MEDIUM) {
            return BoardFactory.createRandom(10, 8);
        }

        return BoardFactory.createRandom(7, 8);
    }

    public void action(final Board board, final int row, final int column) throws ActionException {
        if (board == null) {
            throw new ActionException("Game can't be null empty");
        }

        board.check(row, column);
    }

}

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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.hycom.exception.GameConfigurationException;
import pl.hycom.exception.GameNotFoundException;
import pl.hycom.model.Board;
import pl.hycom.model.Level;
import pl.hycom.model.State;

@Slf4j
@Component
public class GamesHolder {

    @Value("${app.seconds-after-game-expires:600}")
    private int secondsAfterGameExpires;

    @Autowired
    private BoardService boardService;

    private final Map<String, Board> games = new ConcurrentHashMap<>();

    public String initGame(final File file) throws GameConfigurationException {
        Board board = boardService.createBoard(file);

        return initGame(board);
    }

    public String initGame(final String level) throws GameConfigurationException {
        Board board = boardService.createBoard(Level.from(level));

        return initGame(board);
    }

    private String initGame(final Board board) {
        String gameId = generateGameId();

        games.put(gameId, board);
        return gameId;
    }

    private String generateGameId() {
        return UUID.randomUUID().toString();
    }

    public Board getGame(final String id) throws GameNotFoundException {
        if (!games.containsKey(id)) {
            throw new GameNotFoundException("Game with id[" + id + "] not found");
        }
        return games.get(id);
    }

    @Scheduled(fixedRate = 120000)
    public void gamesSupervisor() {
        updateGamesState();
        removeExpiredGames();
    }

    private void updateGamesState() {
        games.entrySet()
                .parallelStream()
                .forEach(entry -> entry.getValue().updateStateIfGameExpired(secondsAfterGameExpires));
    }

    private void removeExpiredGames() {

        Iterator<Entry<String, Board>> entriesIterator = games.entrySet().iterator();
        while (entriesIterator.hasNext()) {
            Entry<String, Board> entry = entriesIterator.next();
            if (entry.getValue().state() == State.FAIL) {
                if (log.isInfoEnabled()) {
                    log.info("Removed session =[" + entry.getKey() + "]");
                }
                entriesIterator.remove();
            }
        }
    }

}

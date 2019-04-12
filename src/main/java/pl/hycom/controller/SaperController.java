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
package pl.hycom.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import pl.hycom.exception.ActionException;
import pl.hycom.exception.GameConfigurationException;
import pl.hycom.exception.GameNotFoundException;
import pl.hycom.model.Board;
import pl.hycom.service.BoardService;
import pl.hycom.service.GamesHolder;

@Slf4j
@Controller
public class SaperController {

    public static final String ATTR_GAME_ID = "gameId";

    @Autowired
    private GamesHolder gamesHolder;

    @Autowired
    private BoardService boardService;

    @GetMapping(value = "/")
    public String index() {
        return "redirect:/start";
    }

    @GetMapping(value = "/start")
    public String start() {
        return "start";
    }

    @GetMapping(value = "/saper")
    public ModelAndView initGameWithLevel(@RequestParam(required = false) final String level, final HttpSession session) throws GameNotFoundException, GameConfigurationException {

        String gameId = gamesHolder.initGame(level);
        session.setAttribute(ATTR_GAME_ID, gameId);

        return initGame(gameId, session);
    }

    @GetMapping(value = "/saper-test")
    public ModelAndView initGameFromFile(final HttpSession session) throws GameNotFoundException, IOException, GameConfigurationException {

        Resource resource = new PathMatchingResourcePatternResolver().getResource("file/test.txt");
        String gameId = gamesHolder.initGame(resource.getFile());

        return initGame(gameId, session);
    }

    private ModelAndView initGame(final String gameId, final HttpSession session) throws GameNotFoundException {
        session.setAttribute(ATTR_GAME_ID, gameId);

        Board board = gamesHolder.getGame(gameId);

        return new ModelAndView("index", "boardContainer", board.prepareDTO());
    }

    @PostMapping(value = "/saper")
    public ModelAndView action(final String action, final HttpSession session) throws GameNotFoundException, ActionException {
        String gameId = (String) session.getAttribute(ATTR_GAME_ID);

        Board board = gamesHolder.getGame(gameId);

        if (StringUtils.isBlank(action)) {
            throw new ActionException("Invalid action[" + action + "]");
        }

        String[] move = StringUtils.split(action, ":");
        if (move.length != 2) {
            throw new ActionException("Invalid action[" + action + "]");
        }

        try {
            int row = Integer.parseInt(move[0]);
            int column = Integer.parseInt(move[1]);

            boardService.action(board, row, column);

            switch (board.state()) {
                case WIN:
                    return new ModelAndView("winner");

                case FAIL:
                    return new ModelAndView("gameOver");

                default:
                    return new ModelAndView("index", "boardContainer", board.prepareDTO());
            }
        } catch (NumberFormatException e) {
            throw new ActionException("Invalid action[" + action + "]");
        }
    }

    @ExceptionHandler(ActionException.class)
    public String handleActionException() {
        if (log.isInfoEnabled()) {
            log.info("User clicked the bomb or action was not valid!");
        }

        return "gameOver";
    }

    @ExceptionHandler({ GameNotFoundException.class, GameConfigurationException.class })
    public String handleGameNotFoundException() {
        if (log.isWarnEnabled()) {
            log.warn("Game not found!");
        }

        return "gameOver";
    }
}

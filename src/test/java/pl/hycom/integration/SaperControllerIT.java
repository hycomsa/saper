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
package pl.hycom.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import pl.hycom.controller.SaperController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SaperControllerIT {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Autowired
    private SaperController saperController;

    @Before
    public void init() {
        mockMvc = standaloneSetup(saperController).build();
        mockHttpSession = new MockHttpSession();
    }

    @Test
    public void game_over() throws Exception {
        mockMvc.perform(get("/saper-test").session(mockHttpSession)).andReturn();

        Exception errorMessage = mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:2")).andReturn().getResolvedException();

        assertEquals("explosion!", errorMessage != null ? errorMessage.getMessage() : "null");
    }

    @Test
    public void winner() throws Exception {
        mockMvc.perform(get("/saper-test").session(mockHttpSession)).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "0:0")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "0:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "0:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "0:7")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "1:4")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "7:0")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "7:6")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "6:6")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "6:7")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "2:1")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "2:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "2:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "2:4")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "3:0")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "3:1")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "3:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "4:0")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "4:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "4:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "4:7")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "5:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "5:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "5:5")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "5:6")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "6:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "6:4")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "7:2")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "7:3")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "7:4")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "2:5")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "5:7")).andReturn();
        mockMvc.perform(post("/saper").session(mockHttpSession).param("action", "4:4")).andExpect(view().name("winner"));

    }
}

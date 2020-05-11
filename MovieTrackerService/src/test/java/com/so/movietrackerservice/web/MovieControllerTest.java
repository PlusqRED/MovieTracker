package com.so.movietrackerservice.web;

import com.so.movietrackerservice.domain.RequestMovie;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.service.MovieTimeCodeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//@SpringBootTest
class MovieControllerTest {

    @Autowired
    private MovieController controller;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private MovieTimeCodeService movieTimeCodeService;

    @Mock
    private RequestMovie requestMovie;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    void testSaveMovie() throws Exception {
        when(movieService.createMovieRatingUsingChromeToken(any(RequestMovie.class), anyString()))
                .thenReturn(true);
        this.mockMvc
                .perform(post("/movies"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, Mock")));
    }

    @Test
    void testSaveMovieWithoutAuth() {

    }

    @Test
    void testSaveMovieTimeCode() {

    }

    @Test
    void testSaveMovieTimeCodeWithoutAuth() {

    }

    @Test
    void testSaveGetUnwatchedMovieTimeCodes() {

    }

    @Test
    void testSaveGetUnwatchedMovieTimeCodesWithoutAuth() {

    }


}

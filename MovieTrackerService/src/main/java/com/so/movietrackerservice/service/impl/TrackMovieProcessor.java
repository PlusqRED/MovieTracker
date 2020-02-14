package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.service.QueryProcessor;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrackMovieProcessor implements QueryProcessor {
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieService movieService;
    private final List<String> processingPatterns = new ArrayList<>() {{
        add("Оценить фильм");
    }};
    private Keyboard ratingKeyboard = new ReplyKeyboardMarkup(
            new String[][]{{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}, {"Отмена"}},
            false,
            true,
            false
    );
    private Keyboard cancelKeyboard = new ReplyKeyboardMarkup(
            new String[][]{{"Отмена"}},
            true,
            false,
            false
    );

    @Override
    public void process(Update update) {
        Session session = new Session(this, Token.MOVIE);
        userSessions.put(update.message().chat().id(), session);
        telegramBotUtils.sendMessage(
                "Введите название фильма, пожалуйста",
                update.message().chat().id(),
                cancelKeyboard
        );
    }

    @Override
    public void continueProcessing(Session session, Update update) {
        if (checkCancel(update, telegramBotUtils, userSessions)) {
            return;
        }
        consumeToken(session, update, (Token) validateToken(session));
    }

    private void consumeToken(Session session, Update update, Token tok) {
        Long chatId = update.message().chat().id();
        switch (tok) {
            case MOVIE: {
                continueFromMovie(session, update, chatId);
                break;
            }
            case RATING: {
                continueFromRating(update, tok, chatId);
                break;
            }
        }
    }

    private Object validateToken(Session session) {
        Object token = session.getToken();
        if (!(token instanceof Token)) {
            throw new IllegalArgumentException("Unsupported token!");
        }
        return token;
    }

    private void continueFromRating(Update update, Token tok, Long chatId) {
        float rating;
        try {
            rating = Float.parseFloat(update.message().text());
        } catch (NumberFormatException ignored) {
            telegramBotUtils.sendMessage("Некорректный рейтинг, попробуйте снова", chatId, ratingKeyboard);
            return;
        }
        Movie movie = Movie.builder().title(tok.movieName).build();
        MovieRating savedMovieRating = movieService.createMovieRating(movie, chatId, rating);
        telegramBotUtils.sendMessage(String.format("%s успешно сохранен", savedMovieRating.toString()), chatId, null);
        userSessions.invalidate(chatId);
    }

    private void continueFromMovie(Session session, Update update, Long chatId) {
        Token nextToken = Token.RATING;
        nextToken.movieName = update.message().text();
        session.setToken(nextToken);
        telegramBotUtils.sendMessage("Окей, введите рейтинг", chatId, ratingKeyboard);
    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }

    private enum Token {
        MOVIE, RATING;

        private String movieName;
    }
}

package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.DialogStage;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrackMovieDialogProcessor implements DialogProcessor {
    private final static String MOVIE = "movie";
    private final static String RATING = "rating";
    private final static String MOVIE_TITLE = "movieTitle";
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieService movieService;
    private final List<String> processingPatterns = Collections.singletonList("Оценить фильм");
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
    public void start(Update update) {
        Session session = new Session(this, DialogStage.builder().stageName(MOVIE).build());
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
        continueDialog(session, update, session.getDialogStage());
    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }

    private void continueDialog(Session session, Update update, DialogStage dialogStage) {
        Long chatId = update.message().chat().id();
        switch (dialogStage.getStageName()) {
            case MOVIE: {
                continueFromMovie(session, update, chatId);
                break;
            }
            case RATING: {
                continueFromRating(update, dialogStage, chatId);
                break;
            }
        }
    }

    private void continueFromRating(Update update, DialogStage dialogStage, Long chatId) {
        float rating;
        try {
            rating = Float.parseFloat(update.message().text().replace(",", "."));
        } catch (NumberFormatException ignored) {
            telegramBotUtils.sendMessage("Некорректный рейтинг, попробуйте снова", chatId, ratingKeyboard);
            return;
        }
        String movieTitle = (String) dialogStage.getMetaData().get(MOVIE_TITLE);
        Movie movie = Movie.builder().title(movieTitle).build();
        MovieRating savedMovieRating = movieService.createMovieRating(movie, chatId, rating);
        telegramBotUtils.sendMessage(String.format("%s успешно сохранен", savedMovieRating.toString()), chatId, null);
        userSessions.invalidate(chatId);
    }

    private void continueFromMovie(Session session, Update update, Long chatId) {
        session.getDialogStage().setStageName(RATING);
        session.getDialogStage().getMetaData().put(MOVIE_TITLE, update.message().text());
        telegramBotUtils.sendMessage("Окей, введите рейтинг", chatId, ratingKeyboard);
    }
}

package com.so.movietrackerservice.service.impl.menu;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.repository.BotUserRepository;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationMovieDialogProcessor implements DialogProcessor {
    private final static double MIN_RECOMMENDATION_RATING = 0.8d;
    private final Cache<Long, Session> userSessions;
    private final MovieService movieService;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieRatingRepository movieRatingRepository;
    private final BotUserRepository botUserRepository;
    private Keyboard keyboard = new ReplyKeyboardMarkup(
            new String[][]{{"По моим оценкам", "Наши рекомендации", "Все"}, {"Отмена"}},
            false,
            false,
            false
    );
    private final List<String> processingPatterns = new ArrayList<>() {{
        add("Рекомендации");
    }};

    @Override
    public void start(Update update) {
        Session session = new Session(this, null);
        userSessions.put(update.message().chat().id(), session);
        telegramBotUtils.sendMessage(
                "Какие рекомендации вы хотели бы получить?",
                update.message().chat().id(),
                keyboard
        );
    }

    @Override
    public void continueProcessing(Session session, Update update) {
        Long chatId = update.message().chat().id();
        if (checkCancel(update, telegramBotUtils, userSessions)) {
            return;
        }
        String response = "Совпадений с другими пользователями не найдено :(";
        switch (update.message().text().trim().toLowerCase()) {
            case "по моим оценкам":
                response = recommendationMoviesFromLocalServiceRatingSystem(chatId);
                break;
            case "наши рекомендации":
                break;
            case "все":
                break;
        }
        telegramBotUtils.sendMessage(response, chatId, null);
        userSessions.invalidate(chatId);
    }


    private String recommendationMoviesFromLocalServiceRatingSystem(Long chatId) {
        List<MovieRating> movieRatings = movieRatingRepository.findAllByBotUserId(chatId, Pageable.unpaged());
        if (!movieRatings.isEmpty()) {
            List<MovieRating> result = movieService.performDatabaseRecommendationAlgorithm(movieRatings, chatId);
            if (result.isEmpty()) {
                return "Совпадений с другими пользователями не найдено :(";
            }
            Map<String, Double> recommendations = result.stream()
                    .distinct()
                    .collect(Collectors.groupingBy(movieRating -> movieRating.getMovie().getTitle(), Collectors.averagingDouble(MovieRating::getRating)));
            return recommendations.entrySet()
                    .stream()
                    .filter(stringDoubleEntry -> stringDoubleEntry.getValue() > MIN_RECOMMENDATION_RATING)
                    .map(movieRatingListEntry -> movieRatingListEntry.getKey() + " - " + String.format("[%.2f]", movieRatingListEntry.getValue()))
                    .collect(Collectors.joining("\n"));
        } else {
            return "Ваш список фильмов пока пуст, пора добавить!";
        }
    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }
}

package com.so.movietrackerservice.service.impl.menu;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.DialogStage;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.domain.themoviedb.TheMovieDbApiMovie;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.service.themoviedb.TheMovieDbApiService;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class RecommendationMovieDialogProcessor implements DialogProcessor {
    private final static String CONTINUE_FROM_MOVIENAME_RECOMMENDATIONS = "по названию ввод названия";
    private final Cache<Long, Session> userSessions;
    private final MovieService movieService;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieRatingRepository movieRatingRepository;
    private final TheMovieDbApiService theMovieDbApiService;
    private final Random random = new Random();
    private final List<String> processingPatterns = new ArrayList<>() {{
        add("Рекомендации");
    }};
    @Value("${min.recommendation.overlaps}")
    private String minRecommendationOverlaps;
    @Value("${min.recommendation.rating}")
    private String minRecommendationRating;
    @Value("${max.recommendation.list.size}")
    private String maxRecommendationListSize;
    private Keyboard keyboard = new ReplyKeyboardMarkup(
            new String[][]{{"По моим оценкам"}, {"The Movie Db рекомендации"}, {"По названию"}, {"Отмена"}},
            false,
            false,
            false
    );

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
        String response;
        if (session.getDialogStage() != null) {
            switch (session.getDialogStage().getStageName()) {
                case CONTINUE_FROM_MOVIENAME_RECOMMENDATIONS:
                    String movieTitle = update.message().text();
                    String dialogResponse = getFormattedTheMovieDbApiMovies(theMovieDbApiService.getRecommendations(movieTitle).get());
                    telegramBotUtils.sendMessage(dialogResponse, chatId, null);
                    userSessions.invalidate(chatId);
                    break;
            }
        } else {
            switch (update.message().text().trim().toLowerCase()) {
                case "по моим оценкам":
                    response = recommendationMoviesFromLocalServiceRatingSystem(chatId);
                    userSessions.invalidate(chatId);
                    telegramBotUtils.sendMessage(response, chatId, null);
                    break;
                case "the movie db рекомендации":
                    response = recommendationMoviesFromTheMovieDbService(chatId);
                    userSessions.invalidate(chatId);
                    telegramBotUtils.sendMessage(response, chatId, null);
                    break;
                case "по названию":
                    session.setCurrentProcessor(this);
                    session.setDialogStage(DialogStage.builder()
                            .stageName(CONTINUE_FROM_MOVIENAME_RECOMMENDATIONS)
                            .build());
                    telegramBotUtils.sendMessage("Введите название фильма", chatId, null);
                    break;
            }
        }
    }

    private String recommendationMoviesFromTheMovieDbService(Long chatId) {
        List<MovieRating> movieRatings = movieRatingRepository.findAllByBotUserIdAndRatingGreaterThanEqual(chatId, Float.parseFloat(minRecommendationRating));
        if (!movieRatings.isEmpty()) {
            List<TheMovieDbApiMovie> theMovieDbApiMovies =
                    theMovieDbApiService.getRecommendations(movieRatings.get(movieRatings.size() - 1).getMovie().getTitle()).get();
            if (theMovieDbApiMovies.isEmpty()) {
                return "Нет рекомендаций по данному названию";
            }
            return getFormattedTheMovieDbApiMovies(theMovieDbApiMovies);
        }
        return "Нет рекомендаций по данному запросу, попробуйте затрекать какие-нибудь фильмы";
    }

    private String getFormattedTheMovieDbApiMovies(List<TheMovieDbApiMovie> theMovieDbApiMovies) {
        return "Рекомендации: \n" + theMovieDbApiMovies.stream()
                .map(TheMovieDbApiMovie::toString)
                .collect(joining("\n"));
    }


    private String recommendationMoviesFromLocalServiceRatingSystem(Long chatId) {
        List<MovieRating> movieRatings = movieRatingRepository.findAllByBotUserId(chatId, Pageable.unpaged());
        if (!movieRatings.isEmpty()) {
            List<MovieRating> result = movieService.performDatabaseRecommendationAlgorithm(
                    movieRatings,
                    chatId,
                    Integer.parseInt(minRecommendationOverlaps),
                    Integer.parseInt(maxRecommendationListSize)
            );
            if (result.isEmpty()) {
                return "Совпадений с другими пользователями не найдено :(";
            }
            Map<String, Double> recommendations = result.stream()
                    .distinct()
                    .collect(Collectors.groupingBy(movieRating -> movieRating.getMovie().getTitle(), Collectors.averagingDouble(MovieRating::getRating)));
            return recommendations.entrySet()
                    .stream()
                    .filter(stringDoubleEntry -> stringDoubleEntry.getValue() > Double.parseDouble(minRecommendationRating))
                    .map(movieRatingListEntry -> movieRatingListEntry.getKey() + " - " + String.format("[%.2f]", movieRatingListEntry.getValue()))
                    .collect(joining("\n"));
        } else {
            return "Ваш список фильмов пока пуст, пора добавить!";
        }
    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }
}

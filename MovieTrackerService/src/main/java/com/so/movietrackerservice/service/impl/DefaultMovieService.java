package com.so.movietrackerservice.service.impl;

import com.so.movietrackerservice.domain.db.BotUser;
import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.repository.BotUserRepository;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.repository.MovieRepository;
import com.so.movietrackerservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultMovieService implements MovieService {
    private final MovieRatingRepository movieRatingRepository;
    private final MovieRepository movieRepository;
    private final BotUserRepository botUserRepository;

    @Override
    public MovieRating createMovieRating(Movie inputMovie, Long chatId, float rating) {
        Optional<MovieRating> optionalMovieRating = movieRatingRepository.findMovieRatingByBotUserIdAndMovieTitleIgnoreCase(chatId, inputMovie.getTitle());
        if (optionalMovieRating.isPresent()) {
            return updateExistingMovieRating(rating, optionalMovieRating.get());
        }
        Optional<Movie> movieOptional = movieRepository.findByTitle(inputMovie.getTitle());
        Movie movie = movieOptional.orElse(inputMovie);
        BotUser botUser = botUserRepository.save(BotUser.builder().id(chatId).lastTracking(LocalDateTime.now()).build());
        return movieRatingRepository.save(MovieRating.builder()
                .botUser(botUser)
                .creationDateTime(LocalDateTime.now())
                .movie(movie)
                .rating(rating)
                .build());

    }

    @Override
    public List<MovieRating> performDatabaseRecommendationAlgorithm(
            List<MovieRating> movieRatings,
            long chatId,
            int minOverlaps,
            int maxRecommendationListSize
    ) {
        List<BotUser> botUsers = botUserRepository.findAll();
        botUsers.removeIf(botUser -> botUser.getId().equals(chatId));
        List<MovieRating> result = new ArrayList<>();
        for (BotUser botUser : botUsers) {
            List<MovieRating> allByBotUserChatId = movieRatingRepository.findAllByBotUserId(botUser.getId(), Pageable.unpaged());
            if (!allByBotUserChatId.isEmpty()) {
                Set<MovieRating> listOverlaps = allByBotUserChatId.stream()
                        .filter(movieRatings::contains)
                        .collect(Collectors.toSet());
                if (listOverlaps.size() >= minOverlaps) {
                    allByBotUserChatId.removeAll(listOverlaps);
                    result.addAll(allByBotUserChatId);
                    if (result.size() >= maxRecommendationListSize) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private MovieRating updateExistingMovieRating(float rating, MovieRating movieRating) {
        movieRating.setCreationDateTime(LocalDateTime.now());
        movieRating.setRating(rating);
        return movieRatingRepository.save(movieRating);
    }
}

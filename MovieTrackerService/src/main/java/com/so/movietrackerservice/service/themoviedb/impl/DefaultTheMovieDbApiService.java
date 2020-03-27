package com.so.movietrackerservice.service.themoviedb.impl;

import com.so.movietrackerservice.domain.themoviedb.TheMovieDbApiMovie;
import com.so.movietrackerservice.domain.themoviedb.TheMovieDbApiResponse;
import com.so.movietrackerservice.service.themoviedb.TheMovieDbApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DefaultTheMovieDbApiService implements TheMovieDbApiService {
    private final RestTemplate restTemplate;
    @Value("${themoviedb.api.recommendations}")
    private String theMovieDbApiRecommendationQuery;
    @Value("${themoviedb.api.search}")
    private String theMovieDbApiSearchQuery;
    private Logger logger = LoggerFactory.getLogger(DefaultTheMovieDbApiService.class);

    @Override
    public Supplier<List<TheMovieDbApiMovie>> getRecommendations(String movieTitle) {
        return () -> {
            try {
                return CompletableFuture
                        .supplyAsync(() -> getForMovieId(movieTitle))
                        .thenComposeAsync(aLong -> CompletableFuture.supplyAsync(() -> getRecommendationsList(aLong)))
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("There was an error when sending TheMovieDp api requests! Look up!");
                e.printStackTrace();
            }
            return Collections.emptyList();
        };
    }

    private List<TheMovieDbApiMovie> getRecommendationsList(Long movieId) {
        return Optional.ofNullable(restTemplate.getForEntity(String.format(theMovieDbApiRecommendationQuery, movieId), TheMovieDbApiResponse.class).getBody())
                .map(TheMovieDbApiResponse::getResults)
                .map(theMovieDbApiMovies -> {
                    theMovieDbApiMovies.sort(Comparator.comparing(TheMovieDbApiMovie::getVoteAverage).reversed());
                    return theMovieDbApiMovies;
                })
                .orElse(Collections.emptyList());

    }

    private Long getForMovieId(String movieTitle) {
        Optional<Long> optionalMovieId = Optional.ofNullable(restTemplate.getForEntity(String.format(theMovieDbApiSearchQuery, movieTitle), TheMovieDbApiResponse.class, movieTitle).getBody())
                .map(TheMovieDbApiResponse::getResults)
                .map(theMovieDbApiMovies -> theMovieDbApiMovies.isEmpty() ? null : theMovieDbApiMovies.get(0))
                .map(TheMovieDbApiMovie::getId);
        return optionalMovieId.orElseThrow(() -> new IllegalStateException("Couldn't extract the movie id"));
    }
}

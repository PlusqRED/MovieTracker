package com.so.movietrackerservice.service;

import com.so.movietrackerservice.domain.RequestMovie;
import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieRating;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MovieService {
    @Transactional
    MovieRating createMovieRating(Movie movie, Long chatId, float rating);

    @Transactional
    List<MovieRating> performDatabaseRecommendationAlgorithm(
            List<MovieRating> movieRatings,
            long chatId,
            int minOverlaps,
            int maxRecommendationListSize
    );

    @Transactional
    boolean createMovieRatingUsingChromeToken(RequestMovie requestMovie, String token);

}

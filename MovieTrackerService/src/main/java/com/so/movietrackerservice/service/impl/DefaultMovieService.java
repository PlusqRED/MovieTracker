package com.so.movietrackerservice.service.impl;

import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.repository.MovieRepository;
import com.so.movietrackerservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultMovieService implements MovieService {
    private final MovieRatingRepository movieRatingRepository;
    private final MovieRepository movieRepository;

    @Override
    public MovieRating createMovieRating(Movie inputMovie, Long chatId, float rating) {
        Optional<MovieRating> optionalMovieRating = movieRatingRepository.findMovieRatingByChatIdAndMovieTitle(chatId, inputMovie.getTitle());
        if (optionalMovieRating.isPresent()) {
            return updateExistingMovieRating(rating, optionalMovieRating.get());
        }
        Optional<Movie> movieOptional = movieRepository.findByTitle(inputMovie.getTitle());
        Movie movie = movieOptional.orElse(inputMovie);
        return movieRatingRepository.save(MovieRating.builder()
                .chatId(chatId)
                .creationDateTime(LocalDateTime.now())
                .movie(movie)
                .rating(rating)
                .build());

    }

    private MovieRating updateExistingMovieRating(float rating, MovieRating movieRating) {
        movieRating.setCreationDateTime(LocalDateTime.now());
        movieRating.setRating(rating);
        return movieRatingRepository.save(movieRating);
    }
}

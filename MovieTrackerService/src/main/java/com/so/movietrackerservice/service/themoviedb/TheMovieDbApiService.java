package com.so.movietrackerservice.service.themoviedb;

import com.so.movietrackerservice.domain.themoviedb.TheMovieDbApiMovie;

import java.util.List;
import java.util.function.Supplier;

public interface TheMovieDbApiService {
    Supplier<List<TheMovieDbApiMovie>> getRecommendations(String movieTitle);
}

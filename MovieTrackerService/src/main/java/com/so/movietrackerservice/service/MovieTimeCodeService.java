package com.so.movietrackerservice.service;


import com.so.movietrackerservice.domain.db.MovieTimeCode;

import java.util.List;

public interface MovieTimeCodeService {
    boolean saveMovieTimeCode(
            String movieTitle,
            Integer hours,
            Integer seconds,
            Integer minutes,
            Boolean watched,
            String token
    );

    List<MovieTimeCode> getAllUnwatchedMovieTimeCodes(String token);
}

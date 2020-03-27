package com.so.movietrackerservice.service.impl;

import com.so.movietrackerservice.domain.db.BotUser;
import com.so.movietrackerservice.domain.db.Movie;
import com.so.movietrackerservice.domain.db.MovieTimeCode;
import com.so.movietrackerservice.repository.BotUserRepository;
import com.so.movietrackerservice.repository.MovieTimeCodeRepository;
import com.so.movietrackerservice.service.MovieTimeCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultMovieTimeCodeService implements MovieTimeCodeService {
    private final MovieTimeCodeRepository movieTimeCodeRepository;
    private final BotUserRepository botUserRepository;

    @Override
    public boolean saveMovieTimeCode(String movieTitle, Integer hours, Integer seconds, Integer minutes, Boolean watched, String token) {
        if (movieTitle == null || token == null) {
            return false;
        }
        Optional<BotUser> botUserOptional = botUserRepository.findByChromeExtensionToken(token);
        if (botUserOptional.isEmpty()) {
            return false;
        } else {
            Optional<MovieTimeCode> byMovieTitleIgnoreCase = movieTimeCodeRepository.findByMovieTitleIgnoreCase(movieTitle);
            MovieTimeCode movieTimeCode;
            LocalDateTime endWatch = watched ? LocalDateTime.now() : null;
            BotUser botUser = botUserOptional.get();
            botUser.setLastTracking(LocalDateTime.now());
            if (byMovieTitleIgnoreCase.isPresent()) {
                movieTimeCode = byMovieTitleIgnoreCase.get();
                movieTimeCode.setBotUser(botUser);
                movieTimeCode.setHours(hours);
                movieTimeCode.setEndWatch(endWatch);
                movieTimeCode.setMinutes(minutes);
                movieTimeCode.setSeconds(seconds);
                movieTimeCode.setWatched(watched);
            } else {
                movieTimeCode = MovieTimeCode.builder()
                        .botUser(botUser)
                        .startWatch(LocalDateTime.now().plus(3, ChronoUnit.HOURS))
                        .endWatch(endWatch)
                        .hours(hours)
                        .minutes(minutes)
                        .seconds(seconds)
                        .watched(watched)
                        .movie(Movie.builder()
                                .title(movieTitle)
                                .build())
                        .build();
            }
            movieTimeCodeRepository.save(movieTimeCode);
        }
        return true;
    }

    @Override
    public List<MovieTimeCode> getAllUnwatchedMovieTimeCodes(String token) {
        Optional<BotUser> byChromeExtensionToken = botUserRepository.findByChromeExtensionToken(token);
        if (byChromeExtensionToken.isEmpty()) {
            throw new IllegalArgumentException("Token is invalid");
        } else {
            return movieTimeCodeRepository.findAllByBotUserChromeExtensionTokenAndWatched(token, false);
        }
    }
}

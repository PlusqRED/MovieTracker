package com.so.movietrackerservice.repository;

import com.so.movietrackerservice.domain.db.MovieTimeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieTimeCodeRepository extends JpaRepository<MovieTimeCode, Long> {
    Optional<MovieTimeCode> findByMovieTitleIgnoreCase(String movieTitle);

    List<MovieTimeCode> findAllByBotUserChromeExtensionTokenAndWatched(String token, boolean watched);

    List<MovieTimeCode> findAllByBotUserIdAndWatched(Long chatId, boolean watched);
}

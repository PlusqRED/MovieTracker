package com.so.movietrackerservice.repository;

import com.so.movietrackerservice.domain.db.MovieRating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {
    Optional<MovieRating> findMovieRatingByBotUserIdAndMovieTitleIgnoreCase(Long chatId, String title);

    List<MovieRating> findAllByBotUserId(Long chatId, Pageable pageable);

    List<MovieRating> findAllByBotUserIdAndRatingGreaterThanEqual(Long chatId, Float minRating);


}

package com.so.movietrackerservice.domain.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Float rating;
    private LocalDateTime creationDateTime;

    @JoinColumn(name = "bot_user_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BotUser botUser;

    @JoinColumn(name = "movie_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Movie movie;

    @Override
    public String toString() {
        return movie.getTitle() + " - [" + rating + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieRating that = (MovieRating) o;
        return rating.equals(that.rating) &&
                movie.equals(that.movie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rating, movie);
    }
}

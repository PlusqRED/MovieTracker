package com.so.movietrackerservice.domain.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private Float rating;
    private LocalDateTime creationDateTime;

    @JoinColumn(name = "movie_id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Movie movie;

    @Override
    public String toString() {
        return movie.getTitle() + " - [" + rating + "]";
    }
}

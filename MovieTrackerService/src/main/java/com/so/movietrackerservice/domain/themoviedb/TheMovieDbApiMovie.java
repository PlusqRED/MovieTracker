package com.so.movietrackerservice.domain.themoviedb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/*{
        "id": 286217,
        "video": false,
        "vote_count": 13613,
        "vote_average": 7.7,
        "title": "The Martian",
        "release_date": "2015-09-30",
        "original_language": "en",
        "original_title": "The Martian",
        "genre_ids": [
        12,
        18,
        878
        ],
        "backdrop_path": "/sy3e2e4JwdAtd2oZGA2uUilZe8j.jpg",
        "adult": false,
        "overview": "During a manned mission to Mars, Astronaut Mark Watney is presumed dead after a fierce storm and left behind by his crew. But Watney has survived and finds himself stranded and alone on the hostile planet. With only meager supplies, he must draw upon his ingenuity, wit and spirit to subsist and find a way to signal to Earth that he is alive.",
        "poster_path": "/5aGhaIHYuQbqlHWvWYqMCnj40y2.jpg",
        "popularity": 45.899
        }*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TheMovieDbApiMovie {
    private Long id;
    private Boolean video;
    @JsonProperty(value = "vote_count")
    private Long voteCount;
    @JsonProperty(value = "vote_average")
    private Float voteAverage;
    private String title;
    @JsonProperty(value = "release_date")
    private LocalDate releaseDate;
    @JsonProperty(value = "original_language")
    private String originalLanguage;
    @JsonProperty(value = "genre_ids")
    private List<Integer> genreIds;
    @JsonProperty(value = "backdrop_path")
    private String backdropPath;
    private Boolean adult;
    private String overview;
    @JsonProperty(value = "poster_path")
    private String posterPath;
    private Float popularity;

    @Override
    public String toString() {
        return title + " - ["
                + (voteAverage == null ? "неизвестно" : voteAverage)
                + "] | "
                + releaseDate.getYear();
    }
}

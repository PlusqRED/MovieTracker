package com.so.movietrackerservice.domain.themoviedb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
        {
        "page": 1,
        "results": [movies],
        "total_pages": 2,
        "total_results": 40
        }
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TheMovieDbApiResponse {
    List<TheMovieDbApiMovie> results;
    private Integer page;
    @JsonProperty(value = "total_pages")
    private Integer totalPages;
    @JsonProperty(value = "total_results")
    private Integer totalResults;
}

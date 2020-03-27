package com.so.movietrackerservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMovieTimeCode {
    @JsonProperty(value = "movieTitle")
    private String movieTitle;
    @JsonProperty(value = "h")
    private String hours;
    @JsonProperty(value = "m")
    private String minutes;
    @JsonProperty(value = "s")
    private String seconds;
    @JsonProperty(value = "watched")
    private String watched;
    @JsonProperty(value = "token")
    private String token;
}

package com.so.movietrackerservice.web;

import com.so.movietrackerservice.domain.RequestMovie;
import com.so.movietrackerservice.domain.RequestMovieTimeCode;
import com.so.movietrackerservice.service.MovieService;
import com.so.movietrackerservice.service.MovieTimeCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final MovieTimeCodeService movieTimeCodeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveMovie(@RequestBody RequestMovie requestMovie, @PathParam("token") String token) {
        if (movieService.createMovieRatingUsingChromeToken(requestMovie, token)) {
            return ResponseEntity.ok("Movie was saved");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }

    @PostMapping("/unwatched")
    public ResponseEntity<String> saveMovieTimeCode(@RequestBody RequestMovieTimeCode requestMovieTimeCode) {
        if (movieTimeCodeService.saveMovieTimeCode(
                requestMovieTimeCode.getMovieTitle(),
                requestMovieTimeCode.getHours() != null ? Integer.parseInt(requestMovieTimeCode.getHours()) : null,
                requestMovieTimeCode.getMinutes() != null ? Integer.parseInt(requestMovieTimeCode.getMinutes()) : null,
                requestMovieTimeCode.getSeconds() != null ? Integer.parseInt(requestMovieTimeCode.getSeconds()) : null,
                requestMovieTimeCode.getWatched() != null && Boolean.parseBoolean(requestMovieTimeCode.getWatched()),
                requestMovieTimeCode.getToken()
        )) {
            return ResponseEntity.ok("Movie time code was saved");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }

    @GetMapping("/unwatched")
    public ResponseEntity<?> getUnwatchedMovieTimeCodes(@PathParam("token") String token) {
        try {
            List<String[]> allUnwatchedMovieTimeCodes = movieTimeCodeService.getAllUnwatchedMovieTimeCodes(token)
                    .stream()
                    .map(e -> new String[]{
                            e.getMovie().getTitle(),
                            String.format("%d:%02d:%02d", e.getHours(), e.getMinutes(), e.getSeconds())
                    }).collect(Collectors.toList());
            return ResponseEntity.ok(allUnwatchedMovieTimeCodes);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }

}

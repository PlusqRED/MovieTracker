package com.so.movietrackerservice.domain.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieTimeCode {
    private final static String newYorkDateTimePattern = "dd.MM.yyyy HH:mm:ss";
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int hours;
    private int minutes;
    private int seconds;
    private LocalDateTime startWatch;
    private LocalDateTime endWatch;
    private boolean watched;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "bot_user_id")
    private BotUser botUser;

    @Override
    public String toString() {
        return movie.getTitle() + " - " + hours + ":" + minutes + ":" + seconds + " | Начато: " + startWatch.format(dateTimeFormatter);
    }
}

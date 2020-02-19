package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationMovieDialogProcessor implements DialogProcessor {
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieRatingRepository movieRatingRepository;

    private final List<String> processingPatterns = new ArrayList<>() {{
        add("Рекомендации");
    }};

    @Override
    public void start(Update update) {
        Session session = new Session(this, null);
        userSessions.put(update.message().chat().id(), session);
        telegramBotUtils.sendMessage(
                "Выберите количество",
                update.message().chat().id(),
                null
        );
    }

    @Override
    public void continueProcessing(Session session, Update update) {

    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }
}

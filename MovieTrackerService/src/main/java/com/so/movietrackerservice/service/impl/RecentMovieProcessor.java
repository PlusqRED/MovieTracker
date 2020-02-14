package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.MovieRating;
import com.so.movietrackerservice.repository.MovieRatingRepository;
import com.so.movietrackerservice.service.QueryProcessor;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class RecentMovieProcessor implements QueryProcessor {
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final MovieRatingRepository movieRatingRepository;
    private final List<String> processingPatterns = new ArrayList<>() {{
        add("Последние");
    }};
    private Keyboard keyboard = new ReplyKeyboardMarkup(
            new String[][]{{"10", "30", "50", "Все"}, {"Отмена"}},
            false,
            false,
            false
    );

    @Override
    public void process(Update update) {
        Session session = new Session(this, Token.AMOUNT);
        userSessions.put(update.message().chat().id(), session);
        telegramBotUtils.sendMessage(
                "Выберите количество",
                update.message().chat().id(),
                keyboard
        );
    }

    @Override
    public void continueProcessing(Session session, Update update) {
        Long chatId = update.message().chat().id();
        if (checkCancel(update, telegramBotUtils, userSessions)) {
            return;
        }
        if (validateToken(session).equals(Token.AMOUNT)) {
            continueFromAmount(update, chatId);
        }
    }

    private void continueFromAmount(Update update, Long chatId) {
        int amount;
        try {
            amount = update.message().text().equalsIgnoreCase("все")
                    ? Integer.MAX_VALUE
                    : Integer.parseInt(update.message().text());
        } catch (NumberFormatException ignored) {
            telegramBotUtils.sendMessage("Некорректное количество, попробуйте снова", chatId, keyboard);
            return;
        }
        String resultList = getRecentMoviesResultString(chatId, amount);
        String response = resultList.isEmpty()
                ? "Список пуст"
                : String.format("Последние фильмы %s: \n%s", amount == Integer.MAX_VALUE ? "Все" : amount, resultList);
        telegramBotUtils.sendMessage(
                response,
                chatId,
                null
        );
        userSessions.invalidate(chatId);
    }

    private String getRecentMoviesResultString(Long chatId, int amount) {
        Pageable pageable = PageRequest.of(0, amount, Sort.by(Sort.Direction.DESC, "creationDateTime"));
        return movieRatingRepository.findAllByChatId(chatId, pageable).stream()
                .map(MovieRating::toString)
                .collect(joining(Strings.LINE_SEPARATOR));
    }

    private Object validateToken(Session session) {
        Object token = session.getToken();
        if (!(token instanceof Token)) {
            throw new IllegalArgumentException("Unsupported token!");
        }
        return token;
    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }

    private enum Token {
        AMOUNT
    }
}

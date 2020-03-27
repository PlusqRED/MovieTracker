package com.so.movietrackerservice.service.impl.menu;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.domain.db.MovieTimeCode;
import com.so.movietrackerservice.repository.BotUserRepository;
import com.so.movietrackerservice.repository.MovieTimeCodeRepository;
import com.so.movietrackerservice.service.BotUserService;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
public class OtherMovieDialogProcessor implements DialogProcessor {
    private final BotUserService botUserService;
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final BotUserRepository botUserRepository;
    private final MovieTimeCodeRepository movieTimeCodeRepository;
    private final List<String> processingPatterns = Collections.singletonList("Другое");
    private Keyboard keyboard = new ReplyKeyboardMarkup(
            new String[][]{{"Недосмотренные фильмы"}, {"Сгенерировать токен для браузера"}, {"Текущий токен"}, {"Отмена"}},
            false,
            false,
            false
    );

    @Override
    public void start(Update update) {
        Session session = new Session(this, null);
        userSessions.put(update.message().chat().id(), session);
        telegramBotUtils.sendMessage(
                "Выберите опцию",
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
        switch (update.message().text().trim().toLowerCase()) {
            case "сгенерировать токен для браузера":
                telegramBotUtils.sendMessage(
                        String.format("Ваш токен, вставьте его в расширение браузера: %s", botUserService.applyAndGetToken(chatId)),
                        chatId,
                        null
                );
                userSessions.invalidate(chatId);
                break;
            case "текущий токен":
                botUserRepository.findById(chatId)
                        .flatMap(botUser -> Optional.ofNullable(botUser.getChromeExtensionToken()))
                        .ifPresentOrElse(token -> telegramBotUtils.sendMessage(
                                String.format("Ваш токен, вставьте его в расширение браузера: %s", token),
                                chatId,
                                null
                        ), () -> telegramBotUtils.sendMessage("Токен не создан либо удален", chatId, null));
                userSessions.invalidate(chatId);
                break;
            case "недосмотренные фильмы":
                List<MovieTimeCode> unwatchedMovies =
                        movieTimeCodeRepository.findAllByBotUserIdAndWatched(chatId, false);
                if (!unwatchedMovies.isEmpty()) {
                    String response = "Список недосмотренных фильмов: \n";
                    response += unwatchedMovies.stream()
                            .map(MovieTimeCode::toString)
                            .collect(joining("\n"));
                    telegramBotUtils.sendMessage(response, chatId, null);
                } else {
                    telegramBotUtils.sendMessage("Список недосмотренных фильмов пуст", chatId, null);
                }
                userSessions.invalidate(chatId);
                break;
        }

    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }
}

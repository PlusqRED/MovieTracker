package com.so.movietrackerservice.service.impl.menu;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.service.BotUserService;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtherMovieDialogProcessor implements DialogProcessor {
    private final BotUserService botUserService;
    private final Cache<Long, Session> userSessions;
    private final TelegramBotUtils telegramBotUtils;
    private final List<String> processingPatterns = Collections.singletonList("Другое");
    private Keyboard keyboard = new ReplyKeyboardMarkup(
            new String[][]{{"Сгенерировать токен для браузера"}, {"Отмена"}},
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
                        String.format("Ваш токен: %s", botUserService.applyAndGetToken(chatId)),
                        chatId,
                        null
                );
                userSessions.invalidate(chatId);
                break;
        }

    }

    @Override
    public List<String> getProcessingPatterns() {
        return processingPatterns;
    }
}

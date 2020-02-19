package com.so.movietrackerservice.service;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.utils.TelegramBotUtils;

import java.util.List;

public interface DialogProcessor {

    void start(Update update);

    void continueProcessing(Session session, Update update);

    List<String> getProcessingPatterns();

    default boolean canProcess(Update update) {
        String text = update.message().text();
        if (text == null) {
            return false;
        }
        return getProcessingPatterns().stream().anyMatch(s -> s.equalsIgnoreCase(text));
    }

    default boolean checkCancel(Update update, TelegramBotUtils telegramBotUtils, Cache<Long, Session> userSessions) {
        Long chatId = update.message().chat().id();
        if ("отмена".equalsIgnoreCase(update.message().text())) {
            telegramBotUtils.sendMessage("Отмена", chatId, null);
            userSessions.invalidate(chatId);
            return true;
        }
        return false;
    }
}

package com.so.movietrackerservice.service;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.model.Update;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.utils.TelegramBotUtils;

import java.util.List;

public interface QueryProcessor {
    void process(Update update);

    void continueProcessing(Session session, Update update);

    List<String> getProcessingPatterns();

    default boolean checkCancel(Update update, TelegramBotUtils telegramBotUtils, Cache<Long, Session> userSessions) {
        Long chatId = update.message().chat().id();
        if (update.message().text().equalsIgnoreCase("отмена")) {
            telegramBotUtils.sendMessage("Отмена", chatId, null);
            userSessions.invalidate(chatId);
            return true;
        }
        return false;
    }
}

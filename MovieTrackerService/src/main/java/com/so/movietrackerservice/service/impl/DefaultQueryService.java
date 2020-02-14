package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.service.QueryProcessor;
import com.so.movietrackerservice.service.QueryService;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultQueryService implements QueryService {
    private final List<QueryProcessor> processors;
    private final TelegramBotUtils telegramBotUtils;
    private final TelegramBot bot;
    private final Cache<Long, Session> userSessions;

    @EventListener(ApplicationReadyEvent.class)
    public void setUpBotBehaviour() {
        bot.setUpdatesListener(this::serviceUpdates);
    }

    private int serviceUpdates(List<Update> updates) {
        updates.stream()
                .filter(update -> update.message().text() != null)
                .forEach(this::inspect);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void inspect(Update update) {
        Long chatId = update.message().chat().id();
        if (!loadSession(chatId, update)) {
            findQueryProcessor(update)
                    .ifPresentOrElse(
                            qProc -> qProc.process(update),
                            () -> telegramBotUtils.sendMessage("Внезапно, команда не поддерживается...", chatId, null)
                    );
        }
    }

    @SneakyThrows
    private boolean loadSession(Long chatId, Update update) {
        Session session = userSessions.getIfPresent(chatId);
        if (session == null) {
            return false;
        }
        session.getCurrentProcessor().continueProcessing(session, update);
        return true;
    }

    private Optional<QueryProcessor> findQueryProcessor(Update update) {
        return processors.stream()
                .filter(queryProcessor -> hasNecessaryPattern(queryProcessor, update.message().text()))
                .findAny();
    }

    private boolean hasNecessaryPattern(QueryProcessor queryProcessor, String query) {
        return queryProcessor.getProcessingPatterns()
                .stream()
                .anyMatch(s -> s.equalsIgnoreCase(query));
    }
}

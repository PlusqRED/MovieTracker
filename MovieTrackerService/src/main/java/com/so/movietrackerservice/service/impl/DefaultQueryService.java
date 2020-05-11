package com.so.movietrackerservice.service.impl;

import com.google.common.cache.Cache;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.service.BotUserService;
import com.so.movietrackerservice.service.DialogProcessor;
import com.so.movietrackerservice.service.QueryService;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultQueryService implements QueryService {
    private final List<DialogProcessor> processors;
    private final TelegramBotUtils telegramBotUtils;
    private final Cache<Long, Session> userSessions;
    private final BotUserService botUserService;
    private final TelegramBot bot;

    @EventListener(ApplicationReadyEvent.class)
    public void setUpBotBehaviour() {
        bot.setUpdatesListener(this::serviceUpdates);
    }

    private int serviceUpdates(List<Update> updates) {
        try {
            updates.stream()
                    .filter(update -> update.message().text() != null)
                    .forEach(this::inspect);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void inspect(Update update) {
        Long chatId = update.message().chat().id();
        botUserService.createUserIfDoesntExist(chatId);
        Session session = userSessions.getIfPresent(chatId);
        if (session != null) {
            session.getCurrentProcessor().continueProcessing(session, update);
        } else {
            findQueryProcessor(update)
                    .ifPresentOrElse(
                            qProc -> qProc.start(update),
                            () -> telegramBotUtils.sendMessage("Внезапно, команда не поддерживается...", chatId, null)
                    );
        }
    }

    private Optional<DialogProcessor> findQueryProcessor(Update update) {
        return processors.stream()
                .filter(queryProcessor -> queryProcessor.canProcess(update))
                .findAny();
    }
}

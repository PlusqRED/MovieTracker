package com.so.movietrackerservice.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.listener.SessionRemovalListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationConfig {

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public Cache<Long, Session> userSessions(SessionRemovalListener sessionRemovalListener) {
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .removalListener(sessionRemovalListener)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}

package com.so.movietrackerservice.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.listener.SessionRemovalListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableSwagger2
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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}

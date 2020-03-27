package com.so.movietrackerservice.service.impl;

import com.so.movietrackerservice.repository.BotUserRepository;
import com.so.movietrackerservice.service.BotUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultBotUserService implements BotUserService {
    private final BotUserRepository botUserRepository;

    @Override
    public String applyAndGetToken(long chatId) {
        String generatedToken = RandomStringUtils.randomAlphabetic(32);
        botUserRepository.findById(chatId).ifPresent(botUser -> {
            botUser.setChromeExtensionToken(generatedToken);
            botUserRepository.save(botUser);
        });
        return generatedToken;
    }
}

package com.so.movietrackerservice.service;

import org.springframework.transaction.annotation.Transactional;

public interface BotUserService {
    String applyAndGetToken(long chatId);

    @Transactional
    void createUserIfDoesntExist(long chatId);
}

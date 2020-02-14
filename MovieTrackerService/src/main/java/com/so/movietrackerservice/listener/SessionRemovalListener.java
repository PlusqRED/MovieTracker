package com.so.movietrackerservice.listener;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.so.movietrackerservice.domain.Session;
import com.so.movietrackerservice.utils.TelegramBotUtils;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionRemovalListener implements RemovalListener<Long, Session> {

    private final TelegramBotUtils telegramBotUtils;

    @Override
    public void onRemoval(RemovalNotification<Long, Session> removalNotification) {
        telegramBotUtils.sendMessage(StringUtil.EMPTY_STRING, removalNotification.getKey(), null);
    }
}

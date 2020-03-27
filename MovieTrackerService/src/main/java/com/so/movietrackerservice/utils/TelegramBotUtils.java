package com.so.movietrackerservice.utils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private final TelegramBot telegramBot;

    public void sendMessage(String text, Long chatId, Keyboard keyboard) {
        SendMessage message = new SendMessage(chatId, text);
        message = keyboard == null ? message.replyMarkup(createDefaultKeyboard()) : message.replyMarkup(keyboard);
        telegramBot.execute(message);
    }

    private Keyboard createDefaultKeyboard() {
        return new ReplyKeyboardMarkup(
                new String[][]{{"Оценить фильм"}, {"Топ"}, {"Последние"}, {"Рекомендации"}, {"Другое"}},
                false,
                false,
                false);
    }
}

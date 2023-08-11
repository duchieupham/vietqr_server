package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramUtil extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(TelegramUtil.class);

    @Override
    public void onUpdateReceived(Update update) {
        //
    }

    @Override
    public String getBotUsername() {
        // Tên người dùng của bot trên Telegram
        return EnvironmentUtil.getTelegramBotUsername();
    }

    @Override
    public String getBotToken() {
        // Token của bot trên Telegram
        return EnvironmentUtil.getTelegramBotToken();
    }

    public boolean sendMsg(String chatId, String textMsg) {
        boolean result = false;
        SendMessage message = new SendMessage(chatId, textMsg);
        try {
            execute(message);
            result = true;
        } catch (TelegramApiException e) {
            if (!e.toString().contains("Unable to deserialize response")) {
                logger.error("ERROR at sendMsg: " + e.toString());
                System.out.println("ERROR at sendMsg: " + e.toString());
                e.printStackTrace();
            } else {
                result = false;
            }
        }
        return result;
    }

}

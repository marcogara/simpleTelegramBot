package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class simpleBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private String chatId;

    public simpleBot() {
        loadConfig();
    }

    private void loadConfig() {
        Properties prop = new Properties();
        try (InputStream input = simpleBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            this.botToken = prop.getProperty("bot.token");
            this.botUsername = prop.getProperty("bot.username");
            this.chatId = prop.getProperty("chat.id");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        System.out.println(update.getMessage().getFrom().getFirstName());

        String mess = update.getMessage().getText();
        String message = "";
        
        if (mess.equals("Yes")) {
            message = "nice.";
        }

        SendMessage response = new SendMessage();
        response.setChatId(this.chatId);
        response.setText(message);

        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    public String getChatId() {
        return this.chatId;
    }
}

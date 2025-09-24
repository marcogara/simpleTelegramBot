package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class simpleBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private String chatId;
    private org.telegram.telegrambots.meta.api.objects.Message lastQuestion;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public simpleBot() {
        loadConfig();
    }

    public void sendQuestion(SendMessage message) {
        try {
            this.lastQuestion = execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
            lastQuestion = null;
        }

        if (mess.equals("No")) {
            message = "Ok, I will remind you in 10 min.";
            if (lastQuestion != null) {
                String originalMessage = lastQuestion.getText();
                System.out.println("User sent 'No' in response to: " + originalMessage);
                SendMessage recallMessage = new SendMessage();
                recallMessage.setChatId(this.chatId);
                recallMessage.setText(originalMessage);
                scheduler.schedule(() -> sendQuestion(recallMessage), 10, TimeUnit.MINUTES);
            }
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

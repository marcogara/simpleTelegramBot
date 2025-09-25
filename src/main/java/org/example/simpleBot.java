package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class simpleBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private String chatId;
    private org.telegram.telegrambots.meta.api.objects.Message lastQuestion;
    private final SupplementState supplementState;

    public simpleBot(SupplementState supplementState) {
        this.supplementState = supplementState;
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
        String responseMessage = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();

            if (userMessage.equalsIgnoreCase("Yes")) {
                    responseMessage = "nice.";
                    lastQuestion = null;
                sendMessage(responseMessage);
            }

            if (userMessage.equalsIgnoreCase("No")) {
                    responseMessage = "Ok, I will remind you in 10 min.";
                    if (lastQuestion != null) {
                        String originalMessage = lastQuestion.getText();
                        System.out.println("User sent 'No' in response to: " + originalMessage);

                        if (originalMessage.equals(SupplementState.MORNING_MESSAGE)) {
                            supplementState.morningMessageSent = false;
                        } else if (originalMessage.equals(SupplementState.NOON_MESSAGE)) {
                            supplementState.noonMessageSent = false;
                        } else if (originalMessage.equals(SupplementState.AFTERNOON_MESSAGE)) {
                            supplementState.afternoonMessageSent = false;
                        } else if (originalMessage.equals(SupplementState.EVENING_MESSAGE)) {
                            supplementState.eveningMessageSent = false;
                        }
                    }
                sendMessage(responseMessage);
            }

            if (userMessage.equalsIgnoreCase("Hi")) {
                    responseMessage = "Hi";
                sendMessage(responseMessage);
            }
            }
        }

    private void sendMessage(String responseMessage) {
        if (!responseMessage.isEmpty()) {
            SendMessage response = new SendMessage();
            response.setChatId(this.chatId);
            response.setText(responseMessage);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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

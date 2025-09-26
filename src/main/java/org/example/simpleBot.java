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

import java.util.Map;

public class simpleBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private String chatId;
    private org.telegram.telegrambots.meta.api.objects.Message lastQuestion;
    private final SupplementState supplementState;

    // State and Command Management
    private final List<String> todoList = new ArrayList<>();
    private Command currentConversation = null;
    private final Map<String, Command> commandRegistry;


    public simpleBot(SupplementState supplementState) {
        this.supplementState = supplementState;
        loadConfig();

        // Initialize Command Registry
        commandRegistry = new java.util.HashMap<>();
        commandRegistry.put("/add", new AddCommand());
        commandRegistry.put("/list", new ListCommand());
        commandRegistry.put("/done", new DoneCommand());
    }

    public List<String> getTodoList() {
        return todoList;
    }

    public void setCurrentConversation(Command command) {
        this.currentConversation = command;
    }

    public Command getCurrentConversation() {
        return currentConversation;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            String commandKey = userMessage.split(" ")[0].toLowerCase();

            if (userMessage.equals("Yes") || userMessage.equals("Hi") || userMessage.equals("No")){
                executeCustomLogic(update);
            } else if (commandKey != null || currentConversation != null) {
                executeCommandLogic(commandKey, update, this);
            }
        }
    }

    public void sendMessage(String responseMessage) {
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

    private void executeCustomLogic(Update update) {
        String userMessage = update.getMessage().getText();
        // Keep existing Yes/No/Hi logic if no command or conversation is active
        if (userMessage.equalsIgnoreCase("Yes")) {
            sendMessage("nice.");
            lastQuestion = null;
            return;
        }

        if (userMessage.equalsIgnoreCase("No")) {
            String responseMessage = "Ok, I will remind you in 10 min.";
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
            return;
        }

        if (userMessage.equalsIgnoreCase("Hi")) {
            sendMessage("Hi");
            return;
        }
    }

    private void executeCommandLogic(String commandKey, Update update, simpleBot simpleBot) {
        Command newCommand = commandRegistry.get(commandKey);

        // If the user's message is a recognized command, execute it.
        // This allows a new command to interrupt an ongoing conversation.
        if (newCommand != null) {
            newCommand.execute(update, this);
            return;
        }

        // If it's not a new command, check if we are in a conversation.
        if (currentConversation != null) {
            currentConversation.execute(update, this);
        }
    }

    public void displayList() {
        for (int i = 0; i < this.getTodoList().size(); i++) {
            this.sendMessage((i+1) + ", " + this.getTodoList().get(i));
        }
    }
}

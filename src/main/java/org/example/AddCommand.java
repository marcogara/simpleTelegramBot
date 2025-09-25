package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class AddCommand implements Command {

    @Override
    public void execute(Update update, simpleBot bot) {
        // Check if we are already in a conversation with this command
        if (bot.getCurrentConversation() == this) {
            // This is the second step: user sent the task description
            String task = update.getMessage().getText();
            bot.getTodoList().add(task);
            bot.sendMessage("Task added: '" + task + "'");
            // End the conversation
            bot.setCurrentConversation(null);
        } else {
            // This is the first step: user sent /add
            bot.sendMessage("What task would you like to add?");
            // Start the conversation by setting this command as the current one
            bot.setCurrentConversation(this);
        }
    }
}
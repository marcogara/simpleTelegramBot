package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class DoneCommand implements Command{
    @Override
    public void execute(Update update, simpleBot bot) {
        if(bot.getCurrentConversation() == this) {
            int task = Integer.parseInt(update.getMessage().getText());
            bot.getTodoList().remove(task-1);

            bot.sendMessage("Task deleted, here is you list: ");
            bot.displayList();

            bot.setCurrentConversation(null);
        } else {
            bot.sendMessage("Here is you list: ");
            bot.displayList();
            bot.sendMessage("To delete press the number of the task you want to delete.");
            bot.setCurrentConversation(this);
        }
    }
}

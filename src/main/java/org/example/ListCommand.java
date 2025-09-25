package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class ListCommand implements Command{
    @Override
    public void execute(Update update, simpleBot bot) {
        if(bot.getCurrentConversation() == this) {
            bot.sendMessage("you want:" + "/add" + ", " + "/done" + " a task?");
        } else {
            bot.sendMessage("Here is you list: ");
            for (int i = 0; i < bot.getTodoList().size(); i++) {
                bot.sendMessage((i+1) + ", " + bot.getTodoList().get(i));
            }
            bot.setCurrentConversation(this);
        }
    }
}

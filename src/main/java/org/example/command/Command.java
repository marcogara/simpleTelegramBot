package org.example.command;

import org.example.simpleBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    void execute(Update update, simpleBot bot);
}
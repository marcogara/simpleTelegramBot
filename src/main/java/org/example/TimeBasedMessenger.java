package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeBasedMessenger implements Runnable {

    private final simpleBot bot;
    private final SupplementState supplementState;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TimeBasedMessenger(simpleBot bot, SupplementState supplementState) {
        this.bot = bot;
        this.supplementState = supplementState;
    }

    @Override
    public void run() {
        // Schedule a task to run every minute to check the time
        scheduler.scheduleAtFixedRate(this::checkTimeAndSendSupplementMessage, 0, 10, TimeUnit.MINUTES);
        // Schedule a task to reset the supplement message sent flag every day at midnight
        scheduler.scheduleAtFixedRate(this::resetSupplementMessageSent, 0, 1, TimeUnit.DAYS);
    }

    private void checkTimeAndSendSupplementMessage() {
        LocalTime now = LocalTime.now();

        if (now.getHour() >= 9 && now.getHour() < 12 && !supplementState.morningMessageSent) {
            sendSupplementMessage(SupplementState.MORNING_MESSAGE);
            supplementState.morningMessageSent = true;
        }

        if (now.getHour() >= 13 && !supplementState.noonMessageSent) {
            sendSupplementMessage(SupplementState.NOON_MESSAGE);
            supplementState.noonMessageSent = true;
        }

        if (now.getHour() >= 15 && !supplementState.afternoonMessageSent) {
            sendSupplementMessage(SupplementState.AFTERNOON_MESSAGE);
            supplementState.afternoonMessageSent = true;
        }

        if (now.getHour() >= 19 && !supplementState.eveningMessageSent) {
            sendSupplementMessage(SupplementState.EVENING_MESSAGE);
            supplementState.eveningMessageSent = true;
        }
    }

    private void sendSupplementMessage(String mess) {
        SendMessage message = new SendMessage();
        message.setChatId(bot.getChatId());
        message.setText(mess);
        bot.sendQuestion(message);
    }

    private void resetSupplementMessageSent() {
        supplementState.morningMessageSent = false;
        supplementState.noonMessageSent = false;
        supplementState.afternoonMessageSent = false;
        supplementState.eveningMessageSent = false;
    }
}

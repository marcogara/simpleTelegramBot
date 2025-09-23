package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeBasedMessenger implements Runnable {

    private final simpleBot bot;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final String MORNING_MESSAGE = "did you take your morning supplement ? Hair Supplement and Creatine";
    private boolean morningMessageSent = false;
    private final String NOON_MESSAGE = "did you take your after lunch supplement ? D3 and B group";
    private boolean noonMessageSent = false;
    private final String AFTERNOON_MESSAGE = "did you take your afternoon supplement ? Kollagen and Iron+C";
    private boolean afternoonMessageSent = false;
    private final String EVENING_MESSAGE = "did you take your evening supplement ? Omega 3 and Magnesium";
    private boolean eveningMessageSent = false;

    public TimeBasedMessenger(simpleBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        // Schedule a task to run every minute to check the time
        scheduler.scheduleAtFixedRate(this::checkTimeAndSendSupplementMessage, 0, 1, TimeUnit.MINUTES);
        // Schedule a task to reset the supplement message sent flag every day at midnight
        scheduler.scheduleAtFixedRate(this::resetSupplementMessageSent, 0, 1, TimeUnit.DAYS);
    }

    private void checkTimeAndSendSupplementMessage() {
        LocalTime now = LocalTime.now();

        if (now.getHour() >= 9 && !morningMessageSent) {
            sendSupplementMessage(MORNING_MESSAGE);
            morningMessageSent = true;
        }

        if (now.getHour() >= 12 && !noonMessageSent) {
            sendSupplementMessage(NOON_MESSAGE);
            noonMessageSent = true;
        }

        if (now.getHour() >= 15 && !afternoonMessageSent) {
            sendSupplementMessage(AFTERNOON_MESSAGE);
            afternoonMessageSent = true;
        }

        if (now.getHour() >= 19 && !eveningMessageSent) {
            sendSupplementMessage(EVENING_MESSAGE);
            eveningMessageSent = true;
        }
    }

    private void sendSupplementMessage(String mess) {
        SendMessage message = new SendMessage();
        message.setChatId(bot.getChatId());
        message.setText(mess);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void resetSupplementMessageSent() {
        this.morningMessageSent = false;
        this.noonMessageSent = false;
        this.afternoonMessageSent = false;
        this.eveningMessageSent = false;
    }
}

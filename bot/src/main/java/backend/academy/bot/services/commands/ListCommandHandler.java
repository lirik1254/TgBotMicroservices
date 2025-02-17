package backend.academy.bot.services.commands;

import backend.academy.bot.clients.TrackClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {
    private final TelegramBot bot;
    private final TrackClient trackClient;

    @Override
    public void handle(Long chatId, String messageText) {
        bot.execute(new SendMessage(chatId, trackClient.getTrackLinks(chatId)));
    }
}

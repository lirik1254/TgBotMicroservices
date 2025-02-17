package backend.academy.bot.services.commands;

import backend.academy.bot.clients.TrackClient;
import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
public class UntrackCommandHandler implements CommandHandler {
    private final TrackClient trackClient;
    private final Map<Long, State> userStates;
    private final TelegramBot bot;

    @Override
    public void handle(Long chatId, String messageText) {
        if (messageText.startsWith("/untrack ") && messageText.split(" ").length == 2) {
            String url = messageText.split(" ")[1];
            bot.execute(new SendMessage(chatId, trackClient.unTrackLink(chatId, url)));
            userStates.put(chatId, State.WAITING_FOR_URL);
        } else {
            bot.execute(new SendMessage(chatId, "Для команды /untrack требуется ввести url"));
        }
    }
}

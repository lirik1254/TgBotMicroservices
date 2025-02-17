package backend.academy.bot.services.commands;

import backend.academy.bot.clients.TrackClient;
import backend.academy.bot.utils.RegexCheck;
import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
public class TrackCommandHandler implements CommandHandler {
    private final TrackClient trackClient;
    private final Map<Long, String> userUrl;
    private final Map<Long, State> userStates;
    private final TelegramBot bot;

    @Override
    public void handle(Long chatId, String messageText) {
        if (messageText.startsWith("/track ") && messageText.split(" ").length > 1) {
            String url = messageText.split(" ")[1];
            if (RegexCheck.checkApi(url)) {
                userUrl.put(chatId, url);
                userStates.put(chatId, State.WAITING_FOR_TAGS);
                bot.execute(new SendMessage(chatId, "Введите теги (опционально).\nЕсли теги не нужны - введите /skip"));
            } else {
                bot.execute(new SendMessage(chatId, "Некорректно введена ссылка, введите команду заново"));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Для команды /track требуется ввести url"));
        }
    }
}

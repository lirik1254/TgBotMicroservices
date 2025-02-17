package backend.academy.bot.services.commands;

import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class WaitingForTagsCommandHandler implements CommandHandler {
    private final Map<Long, Map<String, List<String>>> linkTags;
    private final Map<Long, State> userStates;
    private final TelegramBot bot;
    private final Map<Long, String> userUrl;

    @Override
    public void handle(Long chatId, String messageText) {
        if (!messageText.equals("/skip")) {
            linkTags.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>())
                .put(userUrl.get(chatId), new ArrayList<>(Arrays.asList(messageText.split(" "))));
        }
        userStates.put(chatId, State.WAITING_FOR_FILTERS);
        bot.execute(new SendMessage(chatId, "Введите фильтры (опционально - например, user:dummy)\nЕсли фильтры не нужны - введите /skip"));
    }
}

package backend.academy.bot.services.commands;

import backend.academy.bot.clients.TrackClient;
import backend.academy.bot.utils.RegexCheck;
import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class WaitingForFiltersCommandHandler implements CommandHandler{
    private final Map<Long, Map<String, List<String>>> linkFilters;
    private final Map<Long, String> userUrl;
    private final Map<Long, State> userStates;
    private final TrackClient trackClient;
    private final TelegramBot bot;

    @Override
    public void handle(Long chatId, String messageText) {
        boolean isSkip = messageText.equals("/skip");
        boolean isValidFilter = RegexCheck.checkFilter(messageText);

        if (isSkip || isValidFilter) {
            userStates.put(chatId, State.WAITING_FOR_URL);

            Map<String, List<String>> urlFilters = linkFilters.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>());
            List<String> filters = isSkip ? urlFilters.computeIfAbsent(userUrl.get(chatId), k -> new ArrayList<>())
                : new ArrayList<>(Arrays.asList(messageText.split(" ")));
            urlFilters.put(userUrl.get(chatId), filters);

            bot.execute(new SendMessage(chatId, trackClient.trackLink(chatId, userUrl.get(chatId), null, filters)));
        } else {
            bot.execute(new SendMessage(chatId, "Введите фильтры в формате filter:filter"));
        }
    }
}

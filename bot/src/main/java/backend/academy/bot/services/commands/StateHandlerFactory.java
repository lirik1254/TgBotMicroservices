package backend.academy.bot.services.commands;

import backend.academy.bot.clients.RegistrationClient;
import backend.academy.bot.clients.TrackClient;
import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StateHandlerFactory {
    private final Map<State, CommandHandler> handlerMap;
    private final Map<String, CommandHandler> commandHandlerMap;

    public StateHandlerFactory(
        RegistrationClient registrationClient,
        TrackClient trackClient,
        TelegramBot bot,
        Map<Long, String> userUrl,
        Map<Long, State> userStates,
        Map<Long, Map<String, List<String>>> linkTags,
        Map<Long, Map<String, List<String>>> linkFilters
    ) {
        handlerMap = new HashMap<>();
        commandHandlerMap = new HashMap<>();

        handlerMap.put(State.WAITING_FOR_URL, new TrackCommandHandler(trackClient, userUrl, userStates, bot));
        handlerMap.put(State.WAITING_FOR_TAGS, new WaitingForTagsCommandHandler(linkTags, userStates, bot, userUrl));
        handlerMap.put(State.WAITING_FOR_FILTERS, new WaitingForFiltersCommandHandler(linkFilters, userUrl, userStates, trackClient, bot));
        handlerMap.put(State.START, new StartCommandHandler(registrationClient, bot));
        handlerMap.put(State.HELP, new HelpCommandHandler(bot));
        handlerMap.put(State.LIST, new ListCommandHandler(bot, trackClient));

        commandHandlerMap.put("/start", new StartCommandHandler(registrationClient, bot));
        commandHandlerMap.put("/help", new HelpCommandHandler(bot));
        commandHandlerMap.put("/list", new ListCommandHandler(bot, trackClient));
    }

    public CommandHandler getHandler(State state) {
        return handlerMap.get(state);
    }

    public CommandHandler getCommandHandler(String command) {
        return commandHandlerMap.get(command);
    }

}

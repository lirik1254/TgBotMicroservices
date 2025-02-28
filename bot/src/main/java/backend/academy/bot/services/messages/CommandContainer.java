package backend.academy.bot.services.messages;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import static backend.academy.bot.services.messages.CommandName.HELP;
import static backend.academy.bot.services.messages.CommandName.LIST;
import static backend.academy.bot.services.messages.CommandName.START;
import static backend.academy.bot.services.messages.CommandName.TRACK;
import static backend.academy.bot.services.messages.CommandName.UNTRACK;

@Component
public class CommandContainer {
    private final Map<String, Command> commandMap;
    private final UnknownCommand unknownCommand;

    public CommandContainer(
        StartCommand startCommand,
        ListCommand listCommand,
        HelpCommand helpCommand,
        UnknownCommand unknownCommand,
        TrackCommand trackCommand,
        UntrackCommand untrackCommand) {
        commandMap = new HashMap<>();
        commandMap.put(START.commandName(), startCommand);
        commandMap.put(LIST.commandName(), listCommand);
        commandMap.put(HELP.commandName(), helpCommand);
        commandMap.put(TRACK.commandName(), trackCommand);
        commandMap.put(UNTRACK.commandName(), untrackCommand);
        this.unknownCommand = unknownCommand;
    }

    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}

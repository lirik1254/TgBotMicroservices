package backend.academy.bot.services.messages;

import lombok.Getter;

@Getter
public enum CommandName {
    START("/start"),
    LIST("/list"),
    HELP("/help"),
    TRACK("/track"),
    UNTRACK("/untrack");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
}

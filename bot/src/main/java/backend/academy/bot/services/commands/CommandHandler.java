package backend.academy.bot.services.commands;

public interface CommandHandler {
    void handle(Long chatId, String messageText);
}

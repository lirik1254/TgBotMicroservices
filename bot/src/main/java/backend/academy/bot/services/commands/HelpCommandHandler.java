package backend.academy.bot.services.commands;

import backend.academy.bot.utils.BotMessages;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {
    private final TelegramBot bot;

    @Override
    public void handle(Long chatId, String messageText) {
        bot.execute(new SendMessage(chatId, BotMessages.helpMessage));
    }
}

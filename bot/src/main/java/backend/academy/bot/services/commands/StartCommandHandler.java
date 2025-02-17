package backend.academy.bot.services.commands;

import backend.academy.bot.clients.RegistrationClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private final RegistrationClient registrationClient;
    private final TelegramBot bot;

    @Override
    public void handle(Long chatId, String messageText) {
        bot.execute(new SendMessage(chatId, registrationClient.registerUser(chatId)));
    }
}

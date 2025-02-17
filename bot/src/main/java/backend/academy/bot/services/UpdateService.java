package backend.academy.bot.services;

import backend.academy.bot.BotConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class UpdateService {
    private final TelegramBot bot;
    private final BotConfig botConfig;

    public UpdateService(BotConfig botConfig) {
        bot = new TelegramBot(botConfig.telegramToken());
        this.botConfig = botConfig;
    }

    public void update(ArrayList<Long> tgChatIds, String url, String description) {
        tgChatIds.forEach(id ->
            bot.execute(new SendMessage(id, String.format("Пришло уведомление по url %s\nОписание: %s",
                url, description))));
    }
}

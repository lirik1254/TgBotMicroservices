package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommandsSet {
    private final TelegramBot bot;

    @PostConstruct
    public void registerBotCommand() {
        BotCommand[] commands = {
            new BotCommand("/start", "Запуск бота + регистрация"),
            new BotCommand("/help", "Список команд + описание"),
            new BotCommand("/list", "Посмотреть список отслеживаемых ссылок"),
            new BotCommand("/track", "Начать отслеживание ссылки"),
            new BotCommand("/untrack", "Прекратить отслеживание ссылки")
        };

        SetMyCommands setMyCommands = new SetMyCommands(commands);
        bot.execute(setMyCommands);
    }
}

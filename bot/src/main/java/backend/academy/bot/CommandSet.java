package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandSet {
    private final TelegramBot bot;

    @EventListener(ContextRefreshedEvent.class)
    public void initBotCommands() { // Spring автоматически внедрит бин TelegramBot
        BotCommand[] commands = {
            new BotCommand("/start", "Запуск бота + регистрация"),
            new BotCommand("/help", "Список команд + описание"),
            new BotCommand("/list", "Список отслеживаемых ссылок"),
            new BotCommand("/track", "Начать отслеживание"),
            new BotCommand("/untrack", "Прекратить отслеживание")
        };

        bot.execute(new SetMyCommands(commands));
    }
}

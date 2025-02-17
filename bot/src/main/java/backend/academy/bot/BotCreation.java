package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BotCreation {
    private final BotConfig botConfig;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botConfig.telegramToken());
    }
}

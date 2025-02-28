package backend.academy.bot.services;

import backend.academy.bot.BotConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import general.LogMessages;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static general.LogMessages.chatIdString;

@Service
@Slf4j
public class UpdateService {
    private final TelegramBot bot;
    private final BotConfig botConfig;

    public UpdateService(BotConfig botConfig) {
        bot = new TelegramBot(botConfig.telegramToken());
        this.botConfig = botConfig;
    }

    public void update(List<Long> tgChatIds, String url, String description) {
        try {
            tgChatIds.forEach(id -> {
                log.atInfo()
                    .addKeyValue(chatIdString, id)
                    .addKeyValue(LogMessages.url, url)
                    .setMessage("Отправлено обновление")
                    .log();
                bot.execute(new SendMessage(
                    id, String.format("Пришло уведомление по url %s%nОписание: %s", url, description)));
            });
        } catch (Exception e) {
            log.atInfo()
                .addKeyValue(chatIdString, tgChatIds.getFirst())
                .addKeyValue(LogMessages.url, url)
                .setMessage("Некорректные параметры запроса при отправке обновления")
                .log();
            throw new RuntimeException("Некорректные параметры запроса");
        }
    }
}

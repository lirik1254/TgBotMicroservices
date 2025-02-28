package backend.academy.scrapper.repositories;

import static general.LogMessages.chatIdString;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@SuppressWarnings("RegexpSingleline")
public class RegistrationRepository {
    private final ConcurrentHashMap<Long, Boolean> userRegistration = new ConcurrentHashMap<>();

    public String save(Long chatId) {
        userRegistration.put(chatId, true);
        log.atInfo()
                .addKeyValue(chatIdString, chatId)
                .setMessage("Сохранён чат")
                .log();
        return """
            Приветствую в боте, ты успешно зарегистрирован!

            Этот бот поможет тебе отслеживать контент.
            Чтобы ознакомиться с функционалом бота, введи введи /help""";
    }

    public void delete(Long chatId) {
        userRegistration.remove(chatId);
        log.atInfo().setMessage("Удалён чат").addKeyValue(chatIdString, chatId).log();
    }

    public boolean existById(Long id) {
        return userRegistration.containsKey(id);
    }
}

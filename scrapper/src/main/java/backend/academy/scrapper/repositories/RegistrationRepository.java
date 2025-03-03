package backend.academy.scrapper.repositories;

import static general.LogMessages.CHAT_ID_STRING;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@SuppressWarnings("RegexpSingleline")
public class RegistrationRepository {
    private final ConcurrentHashMap<Long, Boolean> userRegistration = new ConcurrentHashMap<>();

    public void save(Long chatId) {
        userRegistration.put(chatId, true);
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Сохранён чат")
                .log();
    }

    public void delete(Long chatId) {
        userRegistration.remove(chatId);
        log.atInfo()
                .setMessage("Удалён чат")
                .addKeyValue(CHAT_ID_STRING, chatId)
                .log();
    }

    public boolean existById(Long id) {
        return userRegistration.containsKey(id);
    }
}

package backend.academy.scrapper.repositories;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RegistrationRepository {
    private final ConcurrentHashMap<Long, Boolean> userRegistration = new ConcurrentHashMap<>();

    public void save(Long chatId) {
        userRegistration.put(chatId, true);
    }

    public void delete(Long chatId) {
        userRegistration.remove(chatId);
    }

    public boolean existById(Long id) {
        return userRegistration.containsKey(id);
    }

}

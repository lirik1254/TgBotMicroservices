package backend.academy.scrapper.services;

import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.repositories.RegistrationRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public void registerUser(Long chatId) {
        registrationRepository.save(chatId);
    }

    public void deleteUser(Long userId) {
        if (!registrationRepository.existById(userId)) {
            throw new ChatNotFoundException("Чат не существует");
        }
        registrationRepository.delete(userId);
    }

}

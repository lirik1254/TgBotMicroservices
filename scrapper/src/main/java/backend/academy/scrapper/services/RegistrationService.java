package backend.academy.scrapper.services;

import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.repositories.RegistrationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public String registerUser(Long chatId) {
        if (registrationRepository.existById(chatId)) {
            return "Вы уже зарегистрированы";
        } else {
            return registrationRepository.save(chatId);
        }
    }

    public void deleteUser(Long userId) {
        if (!registrationRepository.existById(userId)) {
            log.atInfo()
                    .addKeyValue("chatId", userId)
                    .setMessage("Не существует такого чата")
                    .log();
            throw new ChatNotFoundException("Чат не существует");
        }
        registrationRepository.delete(userId);
    }
}

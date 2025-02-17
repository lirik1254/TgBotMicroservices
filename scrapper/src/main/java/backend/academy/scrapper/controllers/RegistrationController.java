package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping(value = "/tg-chat/{id}", produces = "application/json")
    public String registerUser(@PathVariable Long id) {
        registrationService.registerUser(id);
        return "Чат зарегистрирован";
    }

    @DeleteMapping(value = "/tg-chat/{id}", produces = "application/json")
    public String deleteUser(@PathVariable Long id) {
        registrationService.deleteUser(id);
        return "Чат успешно удалён";
    }

}

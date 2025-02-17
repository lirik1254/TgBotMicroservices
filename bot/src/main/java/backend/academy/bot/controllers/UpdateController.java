package backend.academy.bot.controllers;

import backend.academy.bot.DTO.UpdateDTO;
import backend.academy.bot.services.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateController {
    private final UpdateService updateService;

    @PostMapping(value = "/updates", produces = "application/json")
    public String update(@RequestBody UpdateDTO updateDTO) {
        updateService.update(updateDTO.tgChatIds(), updateDTO.url(), updateDTO.description());
        return "Обновление обработано";
    }
}

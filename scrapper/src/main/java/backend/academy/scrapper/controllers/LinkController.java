package backend.academy.scrapper.controllers;

import backend.academy.scrapper.DTO.LinkDTO;
import backend.academy.scrapper.DTO.ReturnLinkDTO;
import backend.academy.scrapper.services.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @PostMapping(value = "/links", produces = "application/json")
    public String addLink(@RequestHeader("Tg-Chat-Id") Long chatId,
                                          @RequestBody @Valid LinkDTO addRequest) {
        linkService.addLink(chatId, addRequest);
        return "Ссылка успешно добавлена";
    }

    @DeleteMapping(value = "/links", produces = "application/json")
    public String deleteLink(@RequestHeader("Tg-Chat-Id") Long chatId,
                             @RequestBody @Valid LinkDTO deleteRequest) {
        linkService.deleteLink(chatId, deleteRequest);
        return "Ссылка успешно убрана";
    }

    @GetMapping(value = "/links", produces = "application/json")
    public ReturnLinkDTO getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return linkService.getLinks(chatId);
    }
}

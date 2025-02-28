package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.LinkService;
import dto.LinkDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import dto.RemoveLinkRequest;
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
    public LinkResponseDTO addLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody @Valid LinkDTO addRequest) {
        return linkService.addLink(chatId, addRequest);
    }

    @DeleteMapping(value = "/links", produces = "application/json")
    public LinkResponseDTO deleteLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody @Valid RemoveLinkRequest removeLinkRequest) {
        return linkService.deleteLink(chatId, removeLinkRequest.link());
    }

    @GetMapping(value = "/links", produces = "application/json")
    public ListLinksResponseDTO getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return linkService.getLinks(chatId);
    }
}

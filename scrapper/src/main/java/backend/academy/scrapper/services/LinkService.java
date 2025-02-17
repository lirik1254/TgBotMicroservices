package backend.academy.scrapper.services;

import backend.academy.scrapper.DTO.LinkDTO;
import backend.academy.scrapper.DTO.ReturnLinkDTO;
import backend.academy.scrapper.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final LinkCheckService linkCheckService;

    public void addLink(Long chatId, LinkDTO addRequest) {
        linkRepository.save(chatId, addRequest.link(), addRequest.tags(), addRequest.filters());
        linkCheckService.checkForUpdates();
    }

    public void deleteLink(Long chatId, LinkDTO deleteRequest) {
        linkRepository.delete(chatId, deleteRequest.link());
    }

    public ReturnLinkDTO getLinks(Long chatId) {
        ConcurrentHashMap<String, LocalDateTime> userLinks = linkRepository.getAllLinks().get(chatId);
        ConcurrentHashMap<String, List<String>> userTags = linkRepository.getAllTags().get(chatId);
        ConcurrentHashMap<String, List<String>> userFilters = linkRepository.getAllFilters().get(chatId);

        ArrayList<LinkDTO> linkDTOS = new ArrayList<>();

        for (Map.Entry<String, LocalDateTime> userLink : userLinks.entrySet()) {
            linkDTOS.add(new LinkDTO(userLink.getKey(), userTags.get(userLink.getKey()), userFilters.get(userLink.getKey())));
        }

        return new ReturnLinkDTO(linkDTOS, linkDTOS.size());
    }

}

package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.repositories.LinkRepository;
import backend.academy.scrapper.utils.LinkType;
import dto.LinkDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;

    public LinkResponseDTO addLink(Long chatId, LinkDTO addRequest) {
        String link = addRequest.link();
        LinkType linkType = null;
        if (link.contains("github.com")) {
            linkType = LinkType.GITHUB;
        } else {
            linkType = LinkType.STACKOVERFLOW;
        }
        return linkRepository.save(chatId, addRequest.link(), addRequest.tags(), addRequest.filters(), linkType);
    }

    public LinkResponseDTO deleteLink(Long chatId, String link) {
        return linkRepository.delete(chatId, link);
    }

    public ListLinksResponseDTO getLinks(Long chatId) {
        Map<String, LocalDateTime> githubLinks =
                linkRepository.getAllGithubLinks().get(chatId);
        Map<String, Integer> stackOverflowLinks =
                linkRepository.getAllStackOverflowLinks().get(chatId);
        Map<String, List<String>> userTags = linkRepository.getAllTags().get(chatId);
        Map<String, List<String>> userFilters = linkRepository.getAllFilters().get(chatId);

        ArrayList<LinkDTO> linkDTOS = new ArrayList<>();

        if (githubLinks != null) {
            githubLinks.forEach(
                    (link, value) -> linkDTOS.add(new LinkDTO(link, userTags.get(link), userFilters.get(link))));
        }

        if (stackOverflowLinks != null) {
            stackOverflowLinks.forEach(
                    (link, value) -> linkDTOS.add(new LinkDTO(link, userTags.get(link), userFilters.get(link))));
        }

        return new ListLinksResponseDTO(linkDTOS, linkDTOS.size());
    }
}

package backend.academy.scrapper.services;

import backend.academy.scrapper.DTO.Link;
import backend.academy.scrapper.repositories.LinkRepository;
import backend.academy.scrapper.utils.LinkType;
import dto.AddLinkDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import general.RegexCheck;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final RegexCheck regexCheck;

    public LinkResponseDTO addLink(Long chatId, AddLinkDTO addRequest) {
        String link = addRequest.link();
        LinkType linkType = regexCheck.isGithub(link) ? LinkType.GITHUB : LinkType.STACKOVERFLOW;
        return linkRepository.save(chatId, addRequest.link(), addRequest.tags(), addRequest.filters(), linkType);
    }

    public LinkResponseDTO deleteLink(Long chatId, String link) {
        return linkRepository.delete(chatId, link);
    }

    public ListLinksResponseDTO getLinks(Long chatId) {
        List<Link> links = linkRepository.links();
        List<LinkResponseDTO> linkResponseDTOS = new ArrayList<>();

        links.stream()
                .filter(s -> s.userId().equals(chatId))
                .forEach(s -> linkResponseDTOS.add(
                        new LinkResponseDTO(Math.toIntExact(s.userId()), s.url(), s.tags(), s.filters())));
        return new ListLinksResponseDTO(linkResponseDTOS, linkResponseDTOS.size());
    }
}

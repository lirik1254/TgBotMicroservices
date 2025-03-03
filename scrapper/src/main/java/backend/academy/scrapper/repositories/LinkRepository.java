package backend.academy.scrapper.repositories;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.LINK_STRING;

import backend.academy.scrapper.DTO.GithubLink;
import backend.academy.scrapper.DTO.Link;
import backend.academy.scrapper.DTO.StackOverflowLink;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.utils.LinkType;
import dto.LinkResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
@Getter
public class LinkRepository {
    private final ScrapperConfig scrapperConfig;
    private final RegistrationRepository registrationRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;

    List<Link> links = new ArrayList<>();

    public LinkResponseDTO save(Long id, String link, List<String> tags, List<String> filters, LinkType linkType) {
        if (!registrationRepository.existById(id)) {
            registrationRepository.save(id);
        }

        links.stream()
                .filter(s -> s.userId().equals(id) && s.url().equals(link))
                .findFirst()
                .ifPresent(links::remove);

        if (linkType.equals(LinkType.GITHUB)) {
            LocalDateTime lastUpdatedTime = gitHubInfoClient.getLastUpdatedTime(link);
            links.add(new GithubLink(id, link, tags, filters, lastUpdatedTime));
        } else {
            Integer answersCount = stackOverflowClient.getLastUpdatedAnswersCount(link);
            links.add(new StackOverflowLink(id, link, tags, filters, answersCount));
        }

        log.atInfo()
                .addKeyValue(LINK_STRING, link)
                .addKeyValue("chatId", id)
                .setMessage("Сохранена ссылка")
                .log();
        return new LinkResponseDTO(id.intValue(), link, tags, filters);
    }

    public LinkResponseDTO delete(Long id, String link) {
        if (links.stream()
                .filter(s -> s.userId().equals(id) && s.url().equals(link))
                .toList()
                .isEmpty()) {
            log.atError()
                    .addKeyValue(LINK_STRING, link)
                    .addKeyValue(CHAT_ID_STRING, id)
                    .setMessage("Не удалось найти ссылку")
                    .log();
            throw new LinkNotFoundException("Ссылка не найдена");
        } else {
            List<String> retTags = links.stream()
                    .filter(s -> s.userId().equals(id) && s.url().equals(link))
                    .findFirst()
                    .orElseThrow()
                    .tags();
            List<String> retFilters = links.stream()
                    .filter(s -> s.userId().equals(id) && s.url().equals(link))
                    .findFirst()
                    .orElseThrow()
                    .filters();
            links.removeIf(s -> s.userId().equals(id) && s.url().equals(link));
            log.atInfo()
                    .addKeyValue(LINK_STRING, link)
                    .addKeyValue(CHAT_ID_STRING, id)
                    .setMessage("Удалена ссылка")
                    .log();
            return new LinkResponseDTO(id.intValue(), link, retTags, retFilters);
        }
    }

    public List<GithubLink> getGithubLinks() {
        return links.stream()
                .filter(s -> s.getType().equals(LinkType.GITHUB))
                .map(link -> (GithubLink) link)
                .toList();
    }

    public List<StackOverflowLink> getStackOverflowLinks() {
        return links.stream()
                .filter(s -> s.getType().equals(LinkType.STACKOVERFLOW))
                .map(link -> (StackOverflowLink) link)
                .toList();
    }

    //    public Map<Long, Map<String, LocalDateTime>> getAllGithubLinks() {
    //        return
    //    }
    //
    //    public Map<Long, Map<String, Integer>> getAllStackOverflowLinks() {
    //        return stackOverflowLinks;
    //    }
    //
    //    public Map<Long, Map<String, List<String>>> getAllTags() {
    //        return tags;
    //    }
    //
    //    public Map<Long, Map<String, List<String>>> getAllFilters() {
    //        return filters;
    //    }
}

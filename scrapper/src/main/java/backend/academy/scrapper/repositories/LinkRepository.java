package backend.academy.scrapper.repositories;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.utils.LinkType;
import dto.LinkResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import static general.LogMessages.chatIdString;
import static general.LogMessages.linkString;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LinkRepository {
    private final ScrapperConfig scrapperConfig;
    private final RegistrationRepository registrationRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;

    private final Map<Long, Map<String, LocalDateTime>> githubLinks = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Integer>> stackOverflowLinks = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, List<String>>> tags = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, List<String>>> filters = new ConcurrentHashMap<>();

    public LinkResponseDTO save(Long id, String link, List<String> tags, List<String> filters, LinkType linkType) {
        if (!registrationRepository.existById(id)) {
            registrationRepository.save(id);
        }

        if (linkType == LinkType.GITHUB) {
            Map<String, LocalDateTime> githubLinkMap = githubLinks.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
            LocalDateTime localDateTime = gitHubInfoClient.getLastUpdatedTime(link);
            githubLinkMap.put(link, localDateTime);
        } else {
            Map<String, Integer> stackOverflowMap =
                stackOverflowLinks.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
            int lastUpdatedAnswersCount = stackOverflowClient.getLastUpdatedAnswersCount(link);
            stackOverflowMap.put(link, lastUpdatedAnswersCount);
        }

        this.tags.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
        this.tags.get(id).put(link, tags);

        this.filters.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
        this.filters.get(id).put(link, filters);

        log.atInfo()
            .addKeyValue(linkString, link)
            .addKeyValue("chatId", id)
            .setMessage("Сохранена ссылка")
            .log();
        return new LinkResponseDTO(id.intValue(), link, tags, filters);
    }

    public LinkResponseDTO delete(Long id, String link) {
        if (removeLinkFromMap(githubLinks, id, link) || removeLinkFromMap(stackOverflowLinks, id, link)) {
            Map<String, List<String>> retTags = new HashMap<>(tags.getOrDefault(id, new ConcurrentHashMap<>()));
            Map<String, List<String>> retFilters = new HashMap<>(filters.getOrDefault(id, new ConcurrentHashMap<>()));
            tags.getOrDefault(id, new ConcurrentHashMap<>()).remove(link);
            filters.getOrDefault(id, new ConcurrentHashMap<>()).remove(link);
            log.atInfo()
                .addKeyValue(linkString, link)
                .addKeyValue(chatIdString, id)
                .setMessage("Удалена ссылка")
                .log();
            return new LinkResponseDTO(
                id.intValue(),
                link,
                retTags.getOrDefault(link, new ArrayList<>()),
                retFilters.getOrDefault(link, new ArrayList<>()));
        }
        log.atError()
            .addKeyValue(linkString, link)
            .addKeyValue(chatIdString, id)
            .setMessage("Не удалось найти ссылку")
            .log();
        throw new LinkNotFoundException("Ссылка не найдена");
    }

    private <T> boolean removeLinkFromMap(Map<Long, Map<String, T>> links, Long id, String link) {
        if (links.containsKey(id) && links.get(id).containsKey(link)) {
            links.get(id).remove(link);
            return true;
        }
        return false;
    }

    public Map<Long, Map<String, LocalDateTime>> getAllGithubLinks() {
        return githubLinks;
    }

    public Map<Long, Map<String, Integer>> getAllStackOverflowLinks() {
        return stackOverflowLinks;
    }

    public Map<Long, Map<String, List<String>>> getAllTags() {
        return tags;
    }

    public Map<Long, Map<String, List<String>>> getAllFilters() {
        return filters;
    }
}

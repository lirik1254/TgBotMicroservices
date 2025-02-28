package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.repositories.LinkRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkCheckService {
    private final LinkRepository linkRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;
    private final UpdateLinkClient updateLinkClient;

    @Scheduled(fixedRate = 15000)
    public void checkForGithubUpdates() {
        log.info("checkForGithubUpdates начал работать");
        Map<Long, Map<String, LocalDateTime>> links = linkRepository.getAllGithubLinks();

        for (Map.Entry<Long, Map<String, LocalDateTime>> idEntry : links.entrySet()) {
            for (Map.Entry<String, LocalDateTime> urlEntry : idEntry.getValue().entrySet()) {
                LocalDateTime lastSavedTime = urlEntry.getValue();
                LocalDateTime lastUpdatedTime = gitHubInfoClient.getLastUpdatedTime(urlEntry.getKey());

                if (!lastSavedTime.isEqual(lastUpdatedTime)) {
                    updateLinkClient.sendUpdate(idEntry.getKey(), urlEntry.getKey());
                    urlEntry.setValue(lastUpdatedTime);
                }
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void checkForStackOverflowUpdates() {
        log.info("checkForStackOverflowUpdates начал работать");
        Map<Long, Map<String, Integer>> links = linkRepository.getAllStackOverflowLinks();

        for (Map.Entry<Long, Map<String, Integer>> idEntry : links.entrySet()) {
            for (Map.Entry<String, Integer> urlEntry : idEntry.getValue().entrySet()) {
                Integer lastSavedAnswersCount = urlEntry.getValue();
                Integer lastUpdatedAnswersCount = stackOverflowClient.getLastUpdatedAnswersCount(urlEntry.getKey());

                if (!Objects.equals(lastSavedAnswersCount, lastUpdatedAnswersCount)) {
                    updateLinkClient.sendUpdate(idEntry.getKey(), urlEntry.getKey());
                    urlEntry.setValue(lastUpdatedAnswersCount);
                }
            }
        }
    }
}

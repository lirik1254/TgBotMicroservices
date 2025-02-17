package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkCheckService {
    private final LinkRepository linkRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final UpdateLinkClient updateLinkClient;

    @Async
    @Scheduled(fixedRate = 15000)
    public void checkForUpdates() {
        log.info("checkForUpdates start work..");
        ConcurrentHashMap<Long, ConcurrentHashMap<String, LocalDateTime>> links = linkRepository.getAllLinks();

        for (Map.Entry<Long, ConcurrentHashMap<String, LocalDateTime>> idEntry : links.entrySet()) {
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


}

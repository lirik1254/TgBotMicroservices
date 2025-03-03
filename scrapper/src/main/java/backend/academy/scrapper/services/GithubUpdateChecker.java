package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.repositories.LinkRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubUpdateChecker implements UpdateChecker {
    private final LinkRepository linkRepository;
    private final GitHubInfoClient gitHubInfoClient;
    private final UpdateLinkClient updateLinkClient;

    public void checkUpdates() {
        log.info("GitHub update check started");

        linkRepository.getGithubLinks().forEach(link -> {
            LocalDateTime actual = gitHubInfoClient.getLastUpdatedTime(link.url());
            if (actual.isAfter(link.lastUpdate())) {
                updateLinkClient.sendUpdate(link.userId(), link.url());
                link.lastUpdate(actual);
            }
        });
    }
}

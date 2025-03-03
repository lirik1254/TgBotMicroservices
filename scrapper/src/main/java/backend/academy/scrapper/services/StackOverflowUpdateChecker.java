package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {
    private final LinkRepository linkRepository;
    private final StackOverflowClient stackOverflowClient;
    private final UpdateLinkClient updateLinkClient;

    public void checkUpdates() {
        log.info("StackOverflow update check started");

        linkRepository.getStackOverflowLinks().forEach(link -> {
            Integer actual = stackOverflowClient.getLastUpdatedAnswersCount(link.url());
            if (!actual.equals(link.answerCount())) {
                updateLinkClient.sendUpdate(link.userId(), link.url());
                link.answerCount(actual);
            }
        });
    }
}

package backend.academy.scrapper.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkCheckService {
    private final List<UpdateChecker> checkers;

    @Scheduled(fixedRate = 15000)
    public void scheduleAllChecks() {
        checkers.forEach(UpdateChecker::checkUpdates);
    }
}

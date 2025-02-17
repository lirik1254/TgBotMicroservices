package backend.academy.scrapper.repositories;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class LinkRepository {
    private final ScrapperConfig scrapperConfig;
    private final RegistrationRepository registrationRepository;
    private final GitHubInfoClient gitHubInfoClient;

    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, LocalDateTime>> links = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, List<String>>> tags = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, List<String>>> filters = new ConcurrentHashMap<>();

    public void save(Long id, String link, List<String> tags, List<String> filters) {
        if (!registrationRepository.existById(id))
            registrationRepository.save(id);

        ConcurrentHashMap<String, LocalDateTime> linkMap = links.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
        linkMap.put(link, Objects.requireNonNull(gitHubInfoClient.getLastUpdatedTime(link)));

        this.tags.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
        this.tags.get(id).put(link, tags);

        this.filters.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
        this.filters.get(id).put(link, filters);
    }

    public void delete(Long id, String link) {
        links.get(id).remove(link);
        tags.get(id).remove(link);
        filters.get(id).remove(link);
    }


   public ConcurrentHashMap<Long, ConcurrentHashMap<String, LocalDateTime>> getAllLinks() {
        return links;
   }

   public ConcurrentHashMap<Long, ConcurrentHashMap<String, List<String>>> getAllTags() {
        return tags;
   }

   public ConcurrentHashMap<Long, ConcurrentHashMap<String, List<String>>> getAllFilters() {
        return filters;
   }


}

package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GitHubInfoClient {
    private final ScrapperConfig scrapperConfig;

    public LocalDateTime getLastUpdatedTime(String link) {
        WebClient webClient = WebClient.builder()
            .baseUrl(link)
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("Authorization", "Bearer " + scrapperConfig.githubToken())
            .build();

        String response = webClient.get()
            .retrieve()
            .bodyToMono(String.class)
            .block();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonResponse = objectMapper.readTree(response);
            return LocalDateTime.parse(jsonResponse.get("updated_at").toString().replace("\"", "").replace("Z", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

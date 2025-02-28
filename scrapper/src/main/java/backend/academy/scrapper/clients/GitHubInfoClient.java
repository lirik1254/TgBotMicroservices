package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.exceptions.RepositoryNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubInfoClient {
    private final ScrapperConfig scrapperConfig;

    public LocalDateTime getLastUpdatedTime(String link)
            throws RepositoryNotFoundException, HttpMessageNotReadableException {
        RestClient restClient = RestClient.create();
        String response = "";
        try {
            response = restClient
                    .get()
                    .uri(ConvertLinkToApiUtils.convertGithubLinkToApi(link))
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer" + scrapperConfig.githubToken())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            log.atError()
                    .addKeyValue("link", link)
                    .setMessage("Не удалось найти репозиторий")
                    .log();
            throw new RepositoryNotFoundException("Репозиторий не найден");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonResponse = objectMapper.readTree(response);
            return LocalDateTime.parse(
                    jsonResponse.get("updated_at").toString().replace("\"", "").replace("Z", ""));
        } catch (HttpMessageNotReadableException | JsonProcessingException e) {
            log.error("Не удалось прочитать поле updated_at");
            throw new HttpMessageNotReadableException("Не удаётся прочитать поле 'updated_at'");
        }
    }
}

package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StackOverflowClient {
    private final ScrapperConfig scrapperConfig;

    public int getLastUpdatedAnswersCount(String link) {
        RestClient restClient = RestClient.create();

        String response = "";
        try {
            response = restClient
                    .get()
                    .uri(ConvertLinkToApiUtils.convertStackOverflowLinkToApi(link))
                    .attribute("key", scrapperConfig.stackOverflow().key())
                    .attribute("access_token", scrapperConfig.stackOverflow().accessToken())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            log.atError()
                    .addKeyValue("link", link)
                    .setMessage("Не удалось найти вопрос по ссылке")
                    .log();
            throw new QuestionNotFoundException("Не удалось найти вопрос");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            return jsonResponse.get("items").size();
        } catch (Exception e) {
            log.error("Не удалось прочитать поле 'items'");
            throw new HttpMessageNotReadableException("Не удаётся прочитать поле 'items'");
        }
    }
}

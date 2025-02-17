package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class StackOverflowClient {
    private final ScrapperConfig scrapperConfig;

    public int getAnswersCount() {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://api.stackexchange.com/2.3/questions/1399133/answers?site=ru.stackoverflow.com")
            .build();

        String response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam("key", scrapperConfig.stackOverflow().key())
                .queryParam("access_token", scrapperConfig.stackOverflow().accessToken())
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();

        System.out.println(response);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            return jsonResponse.get("items").size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

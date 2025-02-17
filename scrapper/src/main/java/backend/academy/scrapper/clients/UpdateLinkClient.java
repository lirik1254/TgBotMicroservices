package backend.academy.scrapper.clients;

import backend.academy.scrapper.DTO.ApiErrorResponseDTO;
import backend.academy.scrapper.DTO.UpdateDTO;
import backend.academy.scrapper.ScrapperConfig;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateLinkClient {
    private final WebClient webClient;
    private final ScrapperConfig scrapperConfig;

    public UpdateLinkClient(WebClient.Builder webClientBuilder, ScrapperConfig scrapperConfig) {
        this.scrapperConfig = scrapperConfig;
        this.webClient = webClientBuilder.baseUrl(scrapperConfig.baseUrl()).build();
    }

    public String sendUpdate(Long chatId, String link) {
        return webClient.post()
            .uri("/updates", chatId)
            .bodyValue(new UpdateDTO(chatId, link, "Произошло обновление!",
                new ArrayList<>(List.of(chatId))))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response ->
                response.bodyToMono(ApiErrorResponseDTO.class)
                    .map(ApiErrorResponseDTO::description)
                    .flatMap(desc -> Mono.just(new RuntimeException(desc)))
            )
            .bodyToMono(String.class)
            .onErrorResume(RuntimeException.class, e -> Mono.just(e.getMessage()))
            .block();
    }
}

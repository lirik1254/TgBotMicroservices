package backend.academy.bot.clients;

import backend.academy.bot.BotConfig;
import backend.academy.bot.DTO.ApiErrorResponseDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RegistrationClient {
    private final WebClient webClient;
    private final BotConfig botConfig;

    public RegistrationClient(WebClient.Builder webClientBuilder, BotConfig botConfig) {
        this.botConfig = botConfig;
        this.webClient = webClientBuilder.baseUrl(botConfig.baseUrl()).build();
    }

    public String registerUser(Long chatId) {
        return webClient.post()
            .uri("/tg-chat/{id}", chatId)
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

package backend.academy.bot.clients;

import backend.academy.bot.BotConfig;
import backend.academy.bot.DTO.LinkDTO;
import backend.academy.bot.DTO.ReturnLinkDTO;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TrackClient {
    private final WebClient webClient;
    private final BotConfig botConfig;

    public TrackClient(WebClient.Builder webClientBuilder, BotConfig botConfig) {
        this.botConfig = botConfig;
        this.webClient = webClientBuilder.baseUrl(botConfig.baseUrl()).build();
    }

    public String trackLink(Long chatId, String link, List<String> tags, List<String> filters) {
        return webClient.post()
            .uri("/links")
            .header("Tg-Chat-Id", chatId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LinkDTO(link, tags, filters))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(RuntimeException.class, e -> Mono.just(e.getMessage()))
            .block();
    }

    public String unTrackLink(Long chatId, String link) {
        return webClient.method(HttpMethod.DELETE)
            .uri("/links")
            .header("Tg-Chat-Id", chatId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("link", link))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(RuntimeException.class, e -> Mono.just(e.getMessage()))
            .block();
    }

    public String getTrackLinks(Long chatId) {
        return webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", chatId.toString())
            .retrieve()
            .bodyToMono(ReturnLinkDTO.class) // Получаем DTO
            .map(ReturnLinkDTO::toString)
            .onErrorResume(e -> Mono.just("Произошла ошибка: " + e.getMessage())) // В случае ошибки возвращаем строку
            .block();
    }
}

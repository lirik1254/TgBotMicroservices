package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import dto.ApiErrorResponseDTO;
import dto.UpdateDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UpdateLinkClient {
    private final ScrapperConfig scrapperConfig;
    RestClient restClient;

    public UpdateLinkClient(ScrapperConfig scrapperConfig) {
        this.scrapperConfig = scrapperConfig;
        restClient = RestClient.create(scrapperConfig.baseUrl());
    }

    public void sendUpdate(Long chatId, String link) {
        restClient
                .post()
                .uri("/updates")
                .body(new UpdateDTO(chatId, link, "Произошло обновление!", new ArrayList<>(List.of(chatId))))
                .exchange((request, response) -> {
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        ApiErrorResponseDTO apiErrorResponseDTO = response.bodyTo(ApiErrorResponseDTO.class);
                        if (apiErrorResponseDTO != null) {
                            log.error(apiErrorResponseDTO.toString());
                        } else {
                            log.atError()
                                    .addKeyValue("link", link)
                                    .addKeyValue("chatId", chatId)
                                    .setMessage("Не удалось отправить обновление по ссылке")
                                    .log();
                        }
                    }
                    return Mono.empty();
                });
    }
}

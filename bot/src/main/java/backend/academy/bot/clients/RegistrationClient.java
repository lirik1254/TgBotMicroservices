package backend.academy.bot.clients;

import static general.LogMessages.chatIdString;
import static general.LogMessages.chatRegistered;
import static general.LogMessages.status;

import backend.academy.bot.BotConfig;
import dto.ApiErrorResponseDTO;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class RegistrationClient {
    private final RestClient restClient;
    private final BotConfig botConfig;

    public RegistrationClient(BotConfig botConfig) {
        this.botConfig = botConfig;
        this.restClient = RestClient.create(botConfig.baseUrl());
    }

    public String registerUser(Long chatId) {
        return Objects.requireNonNull(
                restClient.post().uri("/tg-chat/{id}", chatId).exchange((request, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        log.atInfo()
                                .addKeyValue(chatIdString, chatId)
                                .setMessage(chatRegistered)
                                .log();
                        return chatRegistered;
                    } else {
                        ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                        if (error != null) {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(status, response.getStatusCode())
                                    .addKeyValue("description", error.description())
                                    .setMessage("Не удалось зарегистрировать чат")
                                    .log();
                            return error.description();
                        } else {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(status, response.getStatusCode())
                                    .setMessage("Не удалось зарегистрировать чат - Не удалось прочитать тело ответа")
                                    .log();
                            return "Ошибка";
                        }
                    }
                }));
    }
}

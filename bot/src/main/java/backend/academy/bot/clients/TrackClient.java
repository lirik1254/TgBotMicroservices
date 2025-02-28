package backend.academy.bot.clients;

import static general.LogMessages.chatIdString;
import static general.LogMessages.error;
import static general.LogMessages.githubComString;
import static general.LogMessages.linkString;
import static general.LogMessages.linksCommandString;
import static general.LogMessages.status;
import static general.LogMessages.tgChatIdString;

import backend.academy.bot.BotConfig;
import dto.ApiErrorResponseDTO;
import dto.LinkDTO;
import dto.ReturnLinkDTO;
import general.LogMessages;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class TrackClient {
    private final RestClient restClient;
    private final BotConfig botConfig;

    public TrackClient(BotConfig botConfig) {
        this.botConfig = botConfig;
        this.restClient = RestClient.create(botConfig.baseUrl());
    }

    public String trackLink(Long chatId, String link, List<String> tags, List<String> filters) {
        return restClient
                .post()
                .uri(linksCommandString)
                .header(tgChatIdString, chatId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LinkDTO(link, tags, filters))
                .exchange((request, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        if (link.contains(githubComString)) {
                            log.atInfo()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .setMessage("Изменения в репозитории у чата теперь отслеживаются")
                                    .log();
                            return "Изменения в репозитории теперь отслеживаются";
                        } else {
                            log.atInfo()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .setMessage("Новые ответы на вопрос у чата теперь отслеживаются")
                                    .log();
                            return "Новые ответы на вопрос теперь отслеживаются";
                        }
                    } else {
                        log.atError()
                                .addKeyValue(linkString, link)
                                .addKeyValue(chatIdString, chatId)
                                .setMessage("Произошла ошибка при отслеживании ссылки")
                                .log();
                        return Optional.ofNullable(response.bodyTo(ApiErrorResponseDTO.class))
                                .map(ApiErrorResponseDTO::description)
                                .orElse(error);
                    }
                });
    }

    public String unTrackLink(Long chatId, String link) {
        return restClient
                .method(HttpMethod.DELETE)
                .uri(linksCommandString)
                .header(tgChatIdString, chatId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(linkString, link))
                .exchange((request, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        if (link.contains(githubComString)) {
                            log.atInfo()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .setMessage("Изменения в репозитории у чата теперь больше не отслеживаются")
                                    .log();
                            return "Изменения в репозитории больше не отслеживается";
                        } else {
                            log.atInfo()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .setMessage("Изменения в вопросе у чата больше не отслеживаются")
                                    .log();
                            return "Изменения в вопросе больше не отслеживаются";
                        }
                    } else {
                        ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                        if (error != null) {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .addKeyValue(status, response.getStatusCode())
                                    .addKeyValue("error", error.description())
                                    .setMessage("Не удалось прекратить отслеживание ссылки")
                                    .log();
                            return error.description();
                        } else {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(linkString, link)
                                    .addKeyValue(status, response.getStatusCode())
                                    .setMessage(
                                            "Не удалось прекратить отслеживание ссылки: Тело ответа не удалось прочитать")
                                    .log();
                            return LogMessages.error;
                        }
                    }
                });
    }

    public String getTrackLinks(Long chatId) {
        return restClient
                .get()
                .uri(linksCommandString)
                .header(tgChatIdString, String.valueOf(chatId))
                .exchange((request, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        ReturnLinkDTO dto = response.bodyTo(ReturnLinkDTO.class);
                        if (dto != null) {
                            String result = dto.toString();
                            log.atInfo()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue("links", result)
                                    .setMessage("Успешный ответ от /links")
                                    .log();
                            return result;
                        } else {
                            log.atWarn()
                                    .addKeyValue(chatIdString, chatId)
                                    .setMessage("Успешный ответ от /links, но тело пустое или невалидное")
                                    .log();
                            return "Не удалось получить список отслеживаемых ссылок";
                        }
                    } else {
                        ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                        if (error != null) {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(status, response.getStatusCode())
                                    .addKeyValue("description", error.description())
                                    .setMessage("Ошибка при запросе /links")
                                    .log();
                            return error.description();
                        } else {
                            log.atError()
                                    .addKeyValue(chatIdString, chatId)
                                    .addKeyValue(status, response.getStatusCode())
                                    .setMessage("Ошибка при запросе /links, тело не удалось прочитать")
                                    .log();
                            return "Произошла ошибка";
                        }
                    }
                });
    }
}

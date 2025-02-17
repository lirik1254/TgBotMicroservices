package backend.academy.bot.services;


import backend.academy.bot.BotConfig;
import backend.academy.bot.clients.RegistrationClient;
import backend.academy.bot.clients.TrackClient;
import backend.academy.bot.utils.BotMessages;
import backend.academy.bot.utils.RegexCheck;
import backend.academy.bot.utils.State;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramBot bot;
    private final RegistrationClient registrationClient;
    private final TrackClient trackClient;


    private final Map<Long, State> userStates = new ConcurrentHashMap<>();
    private final Map<Long, String> userUrl = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, ArrayList<String>>> linkTags = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, ArrayList<String>>> linkFilters = new HashMap<>();

    @PostConstruct
    public void startListening() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null) {
                    handleMessage(update);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleMessage(Update update) {
        Long chatId = update.message().chat().id();
        String messageText = update.message().text();

        if (messageText == null) {
            bot.execute(new SendMessage(chatId, BotMessages.wrongCommand));
            return;
        }

        State currentState = userStates.getOrDefault(chatId, State.WAITING_FOR_URL);

        switch (messageText) {
            case "/start" -> bot.execute(new SendMessage(chatId, registrationClient.registerUser(chatId)));

            case "/help" -> bot.execute(new SendMessage(chatId, BotMessages.helpMessage));

            case "/list" -> bot.execute(new SendMessage(chatId, trackClient.getTrackLinks(chatId)));

            default -> {
                switch (currentState) {
                    case WAITING_FOR_URL -> {
                        if (messageText.startsWith("/track ") && messageText.split(" ").length > 1) {
                            String url = messageText.split(" ")[1];
                            if (RegexCheck.checkApi(url)) {
                                userUrl.put(chatId, url);
                                userStates.put(chatId, State.WAITING_FOR_TAGS);
                                bot.execute(new SendMessage(chatId, "Введите теги (опционально).\nЕсли теги не нужны - введите /skip"));
                            } else {
                                bot.execute(new SendMessage(chatId, "Некорректно введена ссылка, введите команду заново"));
                            }
                        } else if (messageText.startsWith("/untrack ") && messageText.split(" ").length == 2) {
                            String url = messageText.split(" ")[1];
                            bot.execute(new SendMessage(chatId, trackClient.unTrackLink(chatId, url)));
                            userStates.put(chatId, State.WAITING_FOR_URL);
                        } else if (messageText.startsWith("/track")) {
                            bot.execute(new SendMessage(chatId,"Для команды /track требуется ввести url"));
                        } else {
                            bot.execute(new SendMessage(chatId, "Неверная команда"));
                        }
                    }

                    case WAITING_FOR_TAGS -> {
                        if (!messageText.equals("/skip")) {
                            linkTags.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>())
                                .put(userUrl.get(chatId), new ArrayList<>(Arrays.asList(messageText.split(" "))));
                        }
                        userStates.put(chatId, State.WAITING_FOR_FILTERS);
                        bot.execute(new SendMessage(chatId, "Введите фильтры (опционально - например, user:dummy)\n" +
                            "Если фильтры не нужны - введите /skip"));
                    }

                    case WAITING_FOR_FILTERS -> {
                        boolean isSkip = messageText.equals("/skip");
                        boolean isValidFilter = RegexCheck.checkFilter(messageText);

                        if (isSkip || isValidFilter) {
                            userStates.put(chatId, State.WAITING_FOR_URL);

                            Map<String, ArrayList<String>> urlTags = linkTags.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>());
                            ArrayList<String> tags = urlTags.computeIfAbsent(userUrl.get(chatId), k -> new ArrayList<>());

                            Map<String, ArrayList<String>> urlFilters = linkFilters.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>());
                            ArrayList<String> filters;

                            if (isSkip) {
                                filters = urlFilters.computeIfAbsent(userUrl.get(chatId), k -> new ArrayList<>());
                            } else {
                                filters = new ArrayList<>(Arrays.asList(messageText.split(" ")));
                                urlFilters.put(userUrl.get(chatId), filters);
                            }

                            bot.execute(new SendMessage(chatId, trackClient.trackLink(chatId, userUrl.get(chatId), tags, filters)));
                        } else {
                            bot.execute(new SendMessage(chatId,"Введите фильтры в формате filter:filter"));
                        }
                    }
                }
            }
        }
    }
}

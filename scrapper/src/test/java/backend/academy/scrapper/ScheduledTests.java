package backend.academy.scrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.repositories.LinkRepository;
import backend.academy.scrapper.repositories.RegistrationRepository;
import backend.academy.scrapper.services.LinkCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dto.LinkDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduledTests {

    static WireMockServer wireMockServer;

    @Autowired
    public MockMvc mockMvc;

    @MockitoBean
    public GitHubInfoClient gitHubInfoClient;

    @Autowired
    public LinkCheckService linkCheckService;

    @MockitoBean
    public UpdateLinkClient updateLinkClient;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public LinkRepository linkRepository;

    @Autowired
    public RegistrationRepository registrationRepository;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8081));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);

        wireMockServer.stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123|5252"))
                .withRequestBody(matchingJsonPath("$.link", matching(".*github.com.*")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));
    }

    @Test
    @DisplayName("Обновления отправляются только пользователям, которые следят за ссылкой")
    public void test1() throws Exception {
        String firstUserGithubLink = "https://github.com/lirik1254/abTestRepo";
        String secondUserGithubLink = "https://github.com/anotherAuthor/anotherLink";

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        LinkDTO firstRequest = new LinkDTO(firstUserGithubLink, tags, filters);
        LinkDTO secondRequest = new LinkDTO(secondUserGithubLink, tags, filters);

        LocalDateTime returnDateTime = LocalDateTime.of(2020, 1, 10, 10, 10);
        when(gitHubInfoClient.getLastUpdatedTime(any())).thenReturn(returnDateTime);

        mockMvc.perform(MockMvcRequestBuilders.post("/links") // Добавляем 1 github ссылку для пользователя с id 123
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(MockMvcRequestBuilders.post("/links") // Добавляем 2 github ссылку для пользователя с id 5252
                .header("Tg-chat-Id", 5252)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        registrationRepository.save(5050L);

        Map<String, LocalDateTime> firstLinkMap = new HashMap<>(Map.of(firstUserGithubLink, LocalDateTime.now()));
        Map<String, LocalDateTime> secondLinkMap = new HashMap<>(Map.of(secondUserGithubLink, LocalDateTime.now()));

        Map<Long, Map<String, LocalDateTime>> userMap = new HashMap<>();
        userMap.put(123L, firstLinkMap);
        userMap.put(5252L, secondLinkMap);

        ReflectionTestUtils.setField(linkRepository, "githubLinks", userMap);

        linkCheckService.checkForGithubUpdates();

        // 1 пользователю отправляется обновление с 1 ссылкой
        Mockito.verify(updateLinkClient).sendUpdate(123L, firstUserGithubLink);

        // 2 пользователю со 2 ссылкой
        Mockito.verify(updateLinkClient).sendUpdate(5252L, secondUserGithubLink);

        // 5050L который не подписывался на ссылки, ничего не отправляется
        Mockito.verify(updateLinkClient, times(0)).sendUpdate(eq(5050L), anyString());
    }
}

package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.exceptions.RepositoryNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class GithubClientTests {
    static WireMockServer wireMockServer;

    @Autowired
    GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    ConvertLinkToApiUtils convertLinkToApiUtils =
            Mockito.mock(ConvertLinkToApiUtils.class, withSettings().defaultAnswer(invocation -> {
                if (invocation.getMethod().getName().equals("convertGithubLinkToApi")) {
                    return link;
                }
                return RETURNS_DEFAULTS.answer(invocation);
            }));

    private static String link = "http://localhost:8080/repos/lirik1254/abTestRepo";

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterAll
    public static void close() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void BeforeEachSetUp() {
        when(convertLinkToApiUtils.convertGithubLinkToApi(anyString())).thenReturn(link);
    }

    @Test
    @DisplayName("Корректный ответ от github.api")
    public void test1() {
        wireMockServer.stubFor(
                get(urlEqualTo("/repos/lirik1254/abTestRepo"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withStatus(200)
                                        .withBody(
                                                """
                    {
                        "updated_at" : "2025-02-24T12:45:01Z"
                    }""")));

        assertEquals(
                LocalDateTime.of(LocalDate.of(2025, 2, 24), LocalTime.of(12, 45, 1)),
                gitHubInfoClient.getLastUpdatedTime(link));
    }

    @Test
    @DisplayName("Некорректный ответ от github.api с 400 ответом")
    public void test2() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/lirik1254/abTestRepo"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("""
                    incorrect json answer""")));
        assertThrows(RepositoryNotFoundException.class, () -> gitHubInfoClient.getLastUpdatedTime(link));
    }

    @Test
    @DisplayName("Некорректный ответ от github.api с 200 ответом")
    public void test3() {
        wireMockServer.stubFor(get(urlEqualTo("/repos/lirik1254/abTestRepo"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                    incorrect json answer""")));

        assertThrows(HttpMessageNotReadableException.class, () -> gitHubInfoClient.getLastUpdatedTime(link));
    }
}

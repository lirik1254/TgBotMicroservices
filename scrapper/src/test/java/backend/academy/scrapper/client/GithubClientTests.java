package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageNotReadableException;

@SpringBootTest
public class GithubClientTests {
    static WireMockServer wireMockServer;

    @Autowired
    GitHubInfoClient gitHubInfoClient;

    private String link = "http://localhost:8080/repos/lirik1254/abTestRepo";

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

        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertEquals(
                    LocalDateTime.of(LocalDate.of(2025, 2, 24), LocalTime.of(12, 45, 1)),
                    gitHubInfoClient.getLastUpdatedTime(link));
        }
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
        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertThrows(RepositoryNotFoundException.class, () -> gitHubInfoClient.getLastUpdatedTime(link));
        }
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

        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertThrows(HttpMessageNotReadableException.class, () -> gitHubInfoClient.getLastUpdatedTime(link));
        }
    }

    private MockedStatic<ConvertLinkToApiUtils> mockConvertLinkToApiUtils() {
        MockedStatic<ConvertLinkToApiUtils> mockedStatic = Mockito.mockStatic(ConvertLinkToApiUtils.class);
        mockedStatic
                .when(() -> ConvertLinkToApiUtils.convertGithubLinkToApi(anyString()))
                .thenReturn(link);
        return mockedStatic;
    }
}

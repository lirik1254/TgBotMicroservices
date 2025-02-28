package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
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
public class StackOverflowClientTests {
    static WireMockServer wireMockServer;

    @Autowired
    StackOverflowClient stackOverflowClient;

    private String link = "http://localhost:8080/stackoverflow/questions/52";
    private String wireMockStubLink = "/stackoverflow/questions/52";

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
    @DisplayName("Корректный ответ от api.stackexchange.com")
    public void test1() {
        wireMockServer.stubFor(
                get(urlEqualTo(wireMockStubLink))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withStatus(200)
                                        .withBody(
                                                """
                    {
                        "items" : [
                        {
                            "first_item" : "first_item"
                        },
                        {
                            "second_item" : "second_item"
                        }
                        ]
                    }""")));

        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertEquals(2, stackOverflowClient.getLastUpdatedAnswersCount(link));
        }
    }

    @Test
    @DisplayName("Некорректный ответ от api.stackexchange.com с ответом 400")
    public void test2() {
        wireMockServer.stubFor(get(urlEqualTo(wireMockStubLink))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("""
                    incorrect json answer""")));
        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertThrows(QuestionNotFoundException.class, () -> stackOverflowClient.getLastUpdatedAnswersCount(link));
        }
    }

    @Test
    @DisplayName("Некорректный ответ от api.stackexchange.com с 200 ответом")
    public void test3() {
        wireMockServer.stubFor(get(urlEqualTo(wireMockStubLink))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                    incorrect json answer""")));

        try (MockedStatic<ConvertLinkToApiUtils> mocked = mockConvertLinkToApiUtils()) {
            assertThrows(
                    HttpMessageNotReadableException.class, () -> stackOverflowClient.getLastUpdatedAnswersCount(link));
        }
    }

    private MockedStatic<ConvertLinkToApiUtils> mockConvertLinkToApiUtils() {
        MockedStatic<ConvertLinkToApiUtils> mockedStatic = Mockito.mockStatic(ConvertLinkToApiUtils.class);
        mockedStatic
                .when(() -> ConvertLinkToApiUtils.convertStackOverflowLinkToApi(anyString()))
                .thenReturn(link);
        return mockedStatic;
    }
}

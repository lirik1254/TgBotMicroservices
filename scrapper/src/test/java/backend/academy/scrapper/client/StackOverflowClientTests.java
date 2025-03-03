package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@RequiredArgsConstructor
public class StackOverflowClientTests {
    static WireMockServer wireMockServer;

    @MockitoBean
    ConvertLinkToApiUtils convertLinkToApiUtils;

    @Autowired
    StackOverflowClient stackOverflowClient;

    private static String link = "http://localhost:8080/stackoverflow/questions/52";
    private String wireMockStubLink = "/stackoverflow/questions/52";

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @BeforeEach
    public void setUpEach() {
        when(convertLinkToApiUtils.convertStackOverflowLinkToApi(anyString())).thenReturn(link);
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

        assertEquals(2, stackOverflowClient.getLastUpdatedAnswersCount(link));
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

        assertThrows(QuestionNotFoundException.class, () -> stackOverflowClient.getLastUpdatedAnswersCount(link));
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

        assertThrows(HttpMessageNotReadableException.class, () -> stackOverflowClient.getLastUpdatedAnswersCount(link));
    }
}

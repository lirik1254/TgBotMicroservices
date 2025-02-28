package backend.academy.scrapper;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование метода конвертации базовых ссылок в ссылку для запроса по api")
public class ConvertTest {

    @Test
    @DisplayName("Конвертирует базовую ссылку на ГХ репозиторий для запроса по api")
    public void convertGithubLinkToApi() {
        String githubLink = "https://github.com/lirik1254/abTestRepo";
        String githubLinkAnotherFormat = "https://github.com/lirik1254/ANOTHER-FORMAAt-REPo";

        String githubConvertedLink = "https://api.github.com/repos/lirik1254/abTestRepo";
        String githubConvertedLinkAnotherFormat = "https://api.github.com/repos/lirik1254/ANOTHER-FORMAAt-REPo";

        assertEquals(githubConvertedLink, ConvertLinkToApiUtils.convertGithubLinkToApi(githubLink));
        assertEquals(
                githubConvertedLinkAnotherFormat,
                ConvertLinkToApiUtils.convertGithubLinkToApi(githubLinkAnotherFormat));
    }

    @Test
    @DisplayName("Конвертирует базовую ссылку на ru.stackoverflow.com для запроса по api")
    public void convertRuStackOverflowLinkToApi() {
        String stackOverflowLink = "https://ru.stackoverflow.com/questions/52";
        String convertedStackOverflowLink =
                "https://api.stackexchange.com/2.3/questions/52/answers?site=ru.stackoverflow.com";

        assertEquals(
                convertedStackOverflowLink, ConvertLinkToApiUtils.convertStackOverflowLinkToApi(stackOverflowLink));
    }

    @Test
    @DisplayName("Конвертирует базовую ссылку на stackoverflow.com для запроса по api")
    public void convertGeneralStackOverflowLinkToApi() {
        String stackOverflowLink = "https://stackoverflow.com/questions/52";
        String convertedStackoverflowLink =
                "https://api.stackexchange.com/2.3/questions/52/answers?site=stackoverflow.com";

        assertEquals(
                convertedStackoverflowLink, ConvertLinkToApiUtils.convertStackOverflowLinkToApi(stackOverflowLink));
    }
}

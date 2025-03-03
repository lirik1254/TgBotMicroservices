package backend.academy.scrapper.link;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import backend.academy.scrapper.DTO.GithubLink;
import backend.academy.scrapper.DTO.Link;
import backend.academy.scrapper.repositories.LinkRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GetLinkTest {

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Тестирование корректного получения ссылок")
    @DirtiesContext
    public void test0() throws Exception {
        ReflectionTestUtils.setField(
                linkRepository,
                "links",
                new ArrayList<Link>(List.of(new GithubLink(
                        123L,
                        "https://github.com/lirik1254/abTestRepo",
                        List.of("52", "yeey"),
                        List.of("filter:filter"),
                        LocalDateTime.now()))));

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 123).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.links[0].id").value(123))
                .andExpect(jsonPath("$.links[0].url").value("https://github.com/lirik1254/abTestRepo"))
                .andExpect(jsonPath("$.links[0].tags").isArray())
                .andExpect(jsonPath("$.links[0].tags[0]").value("52"))
                .andExpect(jsonPath("$.links[0].tags[1]").value("yeey"))
                .andExpect(jsonPath("$.links[0].filters").isArray())
                .andExpect(jsonPath("$.links[0].filters[0]").value("filter:filter"));
    }

    @Test
    @DisplayName("Тестирование получения 0 ссылок")
    public void test1() throws Exception {
        mockMvc.perform(get("/links").header("Tg-Chat-Id", 123).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(0));
    }

    @Test
    @DisplayName("Тестирование некорректного параметра запроса")
    public void test2() throws Exception {
        mockMvc.perform(get("/links").header("Tg-Chat-Id", "пииссяяят двааа").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter 'Tg-Chat-Id': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"пииссяяятдвааа\""))
                .andExpect(jsonPath("$.stacktrace").isArray())
                .andExpect(jsonPath("$.stacktrace").isNotEmpty());
    }
}

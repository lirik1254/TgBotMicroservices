package backend.academy.scrapper.link;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.repositories.LinkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AddLinkDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AddLinkTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    StackOverflowClient stackOverflowClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LinkRepository linkRepository;

    @Test
    @DisplayName("Тестирование добавления github ссылки")
    public void test1() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        LocalDateTime returnDateTime = LocalDateTime.of(2020, 1, 10, 10, 10);
        when(gitHubInfoClient.getLastUpdatedTime("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(LocalDateTime.of(2020, 1, 10, 10, 10));

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("tag1", "tag2")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        assertFalse(linkRepository.getGithubLinks().stream()
                .filter(s -> s.userId().equals(123L)
                        && s.lastUpdate().equals(returnDateTime)
                        && s.tags().equals(tags)
                        && s.filters().equals(filters))
                .toList()
                .isEmpty());
    }

    @Test
    @DisplayName("Тестирование добавления stackoverflow ссылки")
    public void test2() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String githubLink = "https://stackoverflow.com/questions/34534534";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        when(stackOverflowClient.getLastUpdatedAnswersCount("https://stackoverflow.com/questions/34534534"))
                .thenReturn(52);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("tag1", "tag2")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        assertFalse(linkRepository.getStackOverflowLinks().stream()
                .filter(s -> s.userId().equals(123L)
                        && s.answerCount().equals(52)
                        && s.tags().equals(tags)
                        && s.filters().equals(filters))
                .toList()
                .isEmpty());
    }

    @Test
    @DisplayName("Тестирование добавление одной и той же ссылки у одного и того же пользователя")
    public void test4() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String stackOverflowLink = "https://stackoverflow.com/questions/34534534";
        AddLinkDTO request = new AddLinkDTO(stackOverflowLink, tags, filters);

        when(stackOverflowClient.getLastUpdatedAnswersCount("https://stackoverflow.com/questions/34534534"))
                .thenReturn(52);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("tag1", "tag2")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        assertEquals(
                linkRepository.links().stream()
                        .filter(s -> s.userId().equals(123L) && s.url().equals(stackOverflowLink))
                        .toList()
                        .getFirst()
                        .tags(),
                tags);

        request = new AddLinkDTO(stackOverflowLink, List.of("52"), filters);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("52")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        assertEquals(
                linkRepository.links().stream()
                        .filter(s -> s.userId().equals(123L) && s.url().equals(stackOverflowLink))
                        .toList()
                        .getFirst()
                        .tags(),
                List.of("52"));
    }

    @Test
    @DisplayName("Тестирование на добавление ссылки с некорректными параметрами")
    public void test3() throws Exception {
        LocalDateTime returnDateTime = LocalDateTime.of(2020, 1, 10, 10, 10);
        when(gitHubInfoClient.getLastUpdatedTime("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(returnDateTime);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", "aslkdjf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("52"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.code", notNullValue()))
                .andExpect(jsonPath("$.exceptionName", notNullValue()))
                .andExpect(jsonPath("$.exceptionMessage", notNullValue()))
                .andExpect(jsonPath("$.stacktrace", instanceOf(List.class)))
                .andExpect(jsonPath("$.stacktrace", hasSize(greaterThan(0))));
    }
}

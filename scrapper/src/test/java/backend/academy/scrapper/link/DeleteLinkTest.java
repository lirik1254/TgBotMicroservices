package backend.academy.scrapper.link;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.DTO.Link;
import backend.academy.scrapper.DTO.StackOverflowLink;
import backend.academy.scrapper.repositories.LinkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.RemoveLinkRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
public class DeleteLinkTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LinkRepository linkRepository;

    @Test
    @DisplayName("Удаление ссылки")
    @DirtiesContext
    public void test1() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String stackOverflowlink = "https://stackoverflow.com/questions/34534534";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(stackOverflowlink);

        ReflectionTestUtils.setField(
                linkRepository,
                "links",
                new ArrayList<Link>(List.of(new StackOverflowLink(123L, stackOverflowlink, tags, filters, 52))));

        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeLinkRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.url").value(removeLinkRequest.link()))
                .andExpect(jsonPath("$.tags", hasItems("tag1", "tag2")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        Assertions.assertTrue(linkRepository.links().isEmpty());
    }

    @Test
    @DisplayName("Удаление несуществующей ссылки")
    public void test2() throws Exception {
        assertTrue(linkRepository.links().isEmpty());

        String stackOverflowlink = "https://stackoverflow.com/questions/34534534";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(stackOverflowlink);

        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeLinkRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value("Ссылка не найдена"))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(
                        jsonPath("$.exceptionName").value("backend.academy.scrapper.exceptions.LinkNotFoundException"))
                .andExpect(jsonPath("$.exceptionMessage").value("Ссылка не найдена"))
                .andExpect(jsonPath("$.stacktrace").exists());
    }

    @Test
    @DisplayName("Некорректные параметры запроса")
    public void test3() throws Exception {
        mockMvc.perform(delete("/links").header("Tg-Chat-Id", "asdfasdfd").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter "
                                                + "'Tg-Chat-Id': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"asdfasdfd\""))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}

package backend.academy.scrapper.registration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.repositories.RegistrationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteUserTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    @DisplayName("Пользователь, которого собираются удалить, существует")
    public void test1() throws Exception {
        registrationRepository.save(52L);
        assertTrue(registrationRepository.existById(52L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/52").contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Чат успешно удалён"));

        assertFalse(registrationRepository.existById(52L));
    }

    @Test
    @DisplayName("Пользователь, которого собираются удалить, не существует")
    public void test2() throws Exception {
        assertFalse(registrationRepository.existById(52L));

        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/52").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value("Чат не существует"))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(
                        jsonPath("$.exceptionName").value("backend.academy.scrapper.exceptions.ChatNotFoundException"))
                .andExpect(jsonPath("$.exceptionMessage").value("Чат не существует"))
                .andExpect(jsonPath("$.stacktrace").exists());
    }

    @Test
    @DisplayName("Некорректное тело запроса")
    public void test3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/lakj").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter 'id': Failed to convert value "
                                                + "of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"lakj\""))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}

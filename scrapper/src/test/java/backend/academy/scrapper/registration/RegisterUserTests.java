package backend.academy.scrapper.registration;

import static org.junit.jupiter.api.Assertions.*;
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
public class RegisterUserTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    @DisplayName("Пользователь ещё не зарегистрирован")
    public void test1() throws Exception {
        assertFalse(registrationRepository.existById(52L));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                "/tg-chat/52") // Добавляем 1 github ссылку для пользователя с id 123
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(registrationRepository.existById(52L));
    }

    @Test
    @DisplayName("Пользователь уже был зарегистрирован")
    public void test2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/52").contentType(MediaType.APPLICATION_JSON));

        assertTrue(registrationRepository.existById(52L));

        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/52").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Некорректное тело запроса")
    public void test3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/lakj").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}

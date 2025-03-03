package backend.academy.scrapper;

import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.exceptions.RepositoryNotFoundException;
import dto.ApiErrorResponseDTO;
import general.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ScrapperExceptionHandler {
    public static String CLIENT_400 = "400";
    public static String CLIENT_404 = "404";
    private final ExceptionUtils exceptionUtils;

    @ExceptionHandler({
        NumberFormatException.class,
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponseDTO> handleInvalid(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Некорректные параметры запроса",
                CLIENT_400,
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleQuestionNotFound(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Такого вопроса не существует",
                CLIENT_400,
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleRepositoryNotFound(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Такого репозитория не существует",
                CLIENT_400,
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ChatNotFoundException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleChatNotFound(ChatNotFoundException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Чат не существует",
                CLIENT_404,
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleLinkNotFound(LinkNotFoundException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Ссылка не найдена",
                CLIENT_404,
                ex.getClass().getName(),
                ex.getMessage(),
                exceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

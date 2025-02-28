package backend.academy.scrapper.exceptions;

import static general.LogMessages.client400;
import static general.LogMessages.client404;

import dto.ApiErrorResponseDTO;
import general.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ScrapperExceptionHandler {

    @ExceptionHandler({
        NumberFormatException.class,
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponseDTO> handleInvalid(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Некорректные параметры запроса",
                client400,
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleQuestionNotFound(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Такого вопроса не существует",
                client400,
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleRepositoryNotFound(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Такого репозитория не существует",
                client400,
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ChatNotFoundException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleChatNotFound(ChatNotFoundException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Чат не существует",
                client404,
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleLinkNotFound(LinkNotFoundException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Ссылка не найдена",
                client404,
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

package backend.academy.bot.exceptions;

import backend.academy.bot.utils.ExceptionUtils;
import dto.ApiErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalid(Exception ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
                "Некорректные параметры запроса",
                "400",
                ex.getClass().getName(),
                ex.getMessage(),
                ExceptionUtils.getStacktrace(ex));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

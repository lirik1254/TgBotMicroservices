package backend.academy.scrapper.exceptions;

import backend.academy.scrapper.DTO.ApiErrorResponseDTO;
import backend.academy.scrapper.controllers.RegistrationController;
import backend.academy.scrapper.utils.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {RegistrationController.class})
public class RegistrationExceptionHandler {

    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidId(NumberFormatException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
            "Некорректные параметры запроса",
            "400",
            ex.getClass().getName(),
            ex.getMessage(),
            ExceptionUtils.getStacktrace(ex)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleChatNotFound(ChatNotFoundException ex) {
        ApiErrorResponseDTO errorResponse = new ApiErrorResponseDTO(
            "Чат не существует",
            "404",
            ex.getClass().getName(),
            ex.getMessage(),
            ExceptionUtils.getStacktrace(ex)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

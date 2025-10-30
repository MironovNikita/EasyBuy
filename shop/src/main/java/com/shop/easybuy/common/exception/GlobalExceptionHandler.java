package com.shop.easybuy.common.exception;

import com.shop.easybuy.client.model.payment.ErrorRs;
import com.shop.easybuy.common.exception.dto.ApiError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_VIEW = "error";
    private static final String ERROR_ATTRIBUTE = "apiError";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleObjectNotFoundException(ObjectNotFoundException e, Model model) {
        log.error("Возникло ObjectNotFoundException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<String> handleAccessDeniedException(AccessDeniedException e, Model model) {
        log.error("Возникло AccessDeniedException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                getDateTime()
        );
        addRefererToModel(model, "/login");
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleSQLException(SQLException e, Model model) {
        log.error("Возникло SQLException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                "Ошибка при работе с базой данных: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleConstraintViolationException(ConstraintViolationException e, Model model) {
        log.error("Возникло ConstraintViolationException: {}", e.getMessage(), e);
        Map<String, String> fieldErrors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        vio -> vio.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1 + "; " + msg2
                ));

        ApiError apiError = new ApiError(
                "Ошибка валидации входных данных",
                HttpStatus.BAD_REQUEST.value(),
                getDateTime()
        );

        model.addAttribute("validationErrors", fieldErrors);
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleWebExchangeBindException(WebExchangeBindException e, Model model) {
        log.error("Возникло WebExchangeBindException: {}", e.getMessage(), e);
        Map<String, String> fieldErrors = e.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> {
                            String message = error.getDefaultMessage();
                            return message != null ? message : "Недопустимое значение";
                        },
                        (msg1, msg2) -> msg1 + "; " + msg2
                ));

        ApiError apiError = new ApiError(
                "Ошибка валидации входных данных",
                HttpStatus.BAD_REQUEST.value(),
                getDateTime()
        );

        model.addAttribute("validationErrors", fieldErrors);
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNoHandlerFoundException(ResponseStatusException e, Model model) {
        log.error("Возникло ResponseStatusException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                "Страница не найдена: " + e.getReason(),
                HttpStatus.NOT_FOUND.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(CartEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleCartEmptyException(CartEmptyException e, Model model) {
        log.error("Возникло CartEmptyException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        log.error("Возникло IllegalArgumentException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(model, "/main/items");
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(PaymentFailedException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public Mono<String> handlePaymentFailedException(PaymentFailedException e, Model model) {
        log.error("Возникло PaymentFailedException: {}", e.getMessage(), e);
        ErrorRs errorRs = e.getError();
        String errorMessage = (errorRs != null) ? errorRs.getErrorInfo() : "Произошла неизвестная ошибка совершения платежа.";
        int errorCode = (errorRs != null) ? Integer.parseInt(errorRs.getErrorCode()) : HttpStatus.PAYMENT_REQUIRED.value();

        ApiError apiError = new ApiError(
                errorMessage,
                errorCode,
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(WebClientRequestException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<String> handleWebClientRequestException(WebClientRequestException e, Model model) {
        log.error("Возникло WebClientRequestException: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                "Платёжный сервис временно недоступен: %s. Мы уже работаем над устранением проблемы.".formatted(e.getMessage()),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<String> handleDuplicateKeyException(DuplicateKeyException e, Model model) {
        log.error("Возникло DuplicateKeyException: {}", e.getMessage(), e);
        var errorMessage = e.getMessage();
        String field = null;
        if (errorMessage != null) {
            if (errorMessage.contains("users_email_key")) field = "email";
            else if (errorMessage.contains("users_phone_key")) field = "номер телефона";
        }
        String userMessage = field != null
                ? "Ошибка регистрации. Указанный вами " + field + " уже существует!"
                : "Ошибка регистрации. Указанные вами параметры уже существуют!";

        ApiError apiError = new ApiError(
                userMessage,
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(model, "/register");
        return Mono.just(ERROR_VIEW);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleException(Exception e, Model model) {
        log.error("Возникло необработанное исключение: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                "Произошла внутренняя ошибка сервера. Мы уже работаем над её устранением.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        return Mono.just(ERROR_VIEW);
    }

    private String getDateTime() {
        return DateTimeFormatter.ofPattern(DATE_FORMAT).format(LocalDateTime.now());
    }

    private void addRefererToModel(Model model, String referer) {
        model.addAttribute("referer", referer);
    }
}

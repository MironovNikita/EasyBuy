package com.shop.easybuy.common.exception;

import com.shop.easybuy.common.exception.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    public String handleObjectNotFoundException(ObjectNotFoundException e, Model model, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException e, Model model, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                "Ошибка при работе с базой данных: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e, Model model, HttpServletRequest request) {
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
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException e, Model model, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                "Страница не найдена: " + e.getRequestURL(),
                HttpStatus.NOT_FOUND.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, Model model, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                "HTTP-метод запроса не поддерживается: " + e.getMessage(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                "Статический ресурс не найден: " + e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model, HttpServletRequest request) {
        log.error("Возникло необработанное исключение: {}", e.getMessage(), e);
        ApiError apiError = new ApiError(
                "Произошла внутренняя ошибка сервера. Мы уже работаем над её устранением.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getDateTime()
        );
        model.addAttribute(ERROR_ATTRIBUTE, apiError);
        addRefererToModel(request, model);
        return ERROR_VIEW;
    }

    private String getDateTime() {
        return DateTimeFormatter.ofPattern(DATE_FORMAT).format(LocalDateTime.now());
    }

    private void addRefererToModel(HttpServletRequest request, Model model) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) model.addAttribute("referer", referer);
    }
}

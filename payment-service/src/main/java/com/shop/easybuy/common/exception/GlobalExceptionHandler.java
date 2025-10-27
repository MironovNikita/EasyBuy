package com.shop.easybuy.common.exception;

import com.shop.easybuy.model.payment.ErrorRs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public Mono<ResponseEntity<ErrorRs>> handleDataNotFoundException(DataNotFoundException e) {
        return buildErrorRs(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorRs>> handleException(IllegalArgumentException e) {
        return buildErrorRs(e, HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorRs>> handleException(Exception e) {
        return buildErrorRs(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<ResponseEntity<ErrorRs>> buildErrorRs(Throwable throwable, HttpStatus status) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setErrorInfo(throwable.getMessage());
        errorRs.setErrorCode(String.valueOf(status.value()));

        log.error("Возникла ошибка: {}. Статус ответа: {}", throwable.getMessage(), status.value());

        return Mono.just(ResponseEntity.status(status).body(errorRs));
    }
}

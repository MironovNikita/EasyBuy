package com.shop.easybuy.common.exception;

import com.shop.easybuy.model.ErrorRs;
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
        ErrorRs errorRs = new ErrorRs();
        errorRs.setErrorInfo(e.getMessage());
        errorRs.setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()));

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRs));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorRs>> handleException(IllegalArgumentException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setErrorInfo(e.getMessage());
        errorRs.setErrorCode(String.valueOf(HttpStatus.PAYMENT_REQUIRED.value()));

        return Mono.just(ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorRs));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorRs>> handleException(Exception e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setErrorInfo(e.getMessage());
        errorRs.setErrorCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRs));
    }
}

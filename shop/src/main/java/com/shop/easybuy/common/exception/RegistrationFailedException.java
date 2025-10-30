package com.shop.easybuy.common.exception;

public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message) {
        super("Ошибка регистрации пользователя: " + message);
    }
}

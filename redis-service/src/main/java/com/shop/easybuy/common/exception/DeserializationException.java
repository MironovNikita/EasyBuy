package com.shop.easybuy.common.exception;

public class DeserializationException extends RuntimeException {
    public DeserializationException(String object) {
        super("Ошибка десериализации объекта: %s".formatted(object));
    }
}

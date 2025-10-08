package com.shop.easybuy.common.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String key) {
        super("Данные по ключу %s не были найдены в Redis.".formatted(key));
    }
}

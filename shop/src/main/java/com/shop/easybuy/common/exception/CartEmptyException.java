package com.shop.easybuy.common.exception;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException() {
        super("Невозможно оформить заказ: корзина пуста!");
    }
}

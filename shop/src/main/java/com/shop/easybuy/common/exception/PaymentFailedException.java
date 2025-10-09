package com.shop.easybuy.common.exception;

import com.shop.easybuy.client.model.payment.ErrorRs;
import lombok.Getter;

@Getter
public class PaymentFailedException extends RuntimeException {
    private final ErrorRs error;

    public PaymentFailedException(ErrorRs errorRs) {
        this.error = errorRs;
    }
}

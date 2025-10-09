package com.shop.easybuy.service;

import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public Mono<BalanceRs> getBalance() {

        return paymentRepository
                .getBalance()
                .switchIfEmpty(Mono.error(new DataNotFoundException("balance")))
                .map(b -> new BalanceRs().balance(b));
    }

    public Mono<BalanceRs> purchaseOrder(PaymentRq paymentRq) {
        return paymentRepository
                .decrementBalance(paymentRq.getAmount())
                .map(s -> new BalanceRs().balance(s));
    }
}

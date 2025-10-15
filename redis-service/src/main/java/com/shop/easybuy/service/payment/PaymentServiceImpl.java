package com.shop.easybuy.service.payment;

import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public Mono<BalanceRs> getBalance() {
        return paymentRepository
                .getBalance()
                .switchIfEmpty(Mono.error(new DataNotFoundException("balance")))
                .doOnNext(b -> log.info("Значение баланса успешно извлечено: {}", b))
                .map(b -> new BalanceRs().balance(b));
    }

    public Mono<BalanceRs> purchaseOrder(PaymentRq paymentRq) {
        return paymentRepository
                .decrementBalance(paymentRq.getAmount())
                .map(s -> new BalanceRs().balance(s))
                .doOnSuccess(balanceRs -> log.info("Платёж успешно совершён. Остаток средств на балансе: {}", balanceRs.getBalance()));
    }
}

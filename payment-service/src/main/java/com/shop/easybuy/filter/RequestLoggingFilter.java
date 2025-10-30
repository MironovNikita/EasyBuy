package com.shop.easybuy.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements WebFilter {

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @lombok.NonNull WebFilterChain chain) {
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnTerminate(() -> {
                    long endTime = System.currentTimeMillis();
                    String type = exchange.getRequest().getMethod().toString();
                    String uri = exchange.getRequest().getURI().toString();
                    log.info("{}-запрос {} выполнен за {} мс", type, uri, endTime - startTime);
                });
    }
}

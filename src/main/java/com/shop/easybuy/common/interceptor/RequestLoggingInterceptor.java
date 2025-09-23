package com.shop.easybuy.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnTerminate(() -> {
                    long endTime = System.currentTimeMillis();
                    String uri = exchange.getRequest().getURI().toString();
                    String path = exchange.getRequest().getURI().getPath();
                    log.info("Запрос {} {} выполнен за {} мс.", path, uri, endTime - startTime);
                });
    }
}

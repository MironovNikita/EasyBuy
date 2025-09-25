package com.shop.easybuy.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class RequestLoggingFilter implements WebFilter {

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/easy-buy/images/",
            "/easy-buy/favicon"
    );

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (EXCLUDE_PATHS.stream().anyMatch(path::contains)) return chain.filter(exchange);

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnTerminate(() -> {
                    long endTime = System.currentTimeMillis();
                    String uri = exchange.getRequest().getURI().toString();
                    log.info("Запрос {} выполнен за {} мс.", uri, endTime - startTime);
                });
    }
}

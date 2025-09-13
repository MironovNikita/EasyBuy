package com.shop.easybuy.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> startTimeLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        startTimeLocal.set(startTime);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        long startTime = startTimeLocal.get();
        long endTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("Запрос {} {} выполнен за {} мс.", method, uri, endTime - startTime);

        startTimeLocal.remove();
    }
}

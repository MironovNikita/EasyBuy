package com.shop.easybuy.common.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityUserContextHandler {

    public Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null
                        && authentication.isAuthenticated()
                        && authentication.getPrincipal() instanceof CustomUserDetails)
                .map(authentication -> ((CustomUserDetails) authentication.getPrincipal()).getUserId());
    }

    public Mono<Void> checkUserIdOrThrow(Long userId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    Authentication authentication = context.getAuthentication();
                    if (authentication == null || !authentication.isAuthenticated())
                        return Mono.error(new AccessDeniedException("Доступ запрещён. Требуется авторизация."));

                    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
                    if (principal == null || !principal.getUserId().equals(userId))
                        return Mono.error(new AccessDeniedException("Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"));

                    return Mono.empty();
                });

    }
}

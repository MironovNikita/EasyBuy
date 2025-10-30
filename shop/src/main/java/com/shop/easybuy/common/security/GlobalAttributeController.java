package com.shop.easybuy.common.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalAttributeController {

    @ModelAttribute("isAuthenticated")
    public Mono<Boolean> isAuthenticated() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    var auth = securityContext.getAuthentication();

                    return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
                })
                .defaultIfEmpty(false);
    }
}

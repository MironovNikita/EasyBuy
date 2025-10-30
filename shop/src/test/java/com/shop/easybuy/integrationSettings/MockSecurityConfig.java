package com.shop.easybuy.integrationSettings;

import com.shop.easybuy.common.security.SecurityUserContextHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Profile("test")
@TestConfiguration
public class MockSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityUserContextHandler securityUserContextHandler() {
        return new SecurityUserContextHandler() {
            @Override
            public Mono<Long> getCurrentUserId() {
                return Mono.just(1L);
            }

            @Override
            public Mono<Boolean> checkUserIdOrThrow(Long userId) {
                return Mono.just(true);
            }
        };
    }
}

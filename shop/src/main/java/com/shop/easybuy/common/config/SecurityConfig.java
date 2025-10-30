package com.shop.easybuy.common.config;

import com.shop.easybuy.common.security.SecureBase64Converter;
import com.shop.easybuy.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@Profile("!test")
@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${spring.webflux.base-path}")
    private String contextPath;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         UserRepository userRepository,
                                                         SecureBase64Converter converter) {

        ServerAuthenticationSuccessHandler successHandler = (webFilterExchange, authentication) -> {
            String email = authentication.getName();

            return userRepository.findUserByEmail(converter.encrypt(email))
                    .flatMap(user -> webFilterExchange.getExchange().getSession()
                            .doOnNext(session ->
                                    session.getAttributes().put("currentName", user.getName())
                            )
                            .then(Mono.fromRunnable(() -> {
                                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                                response.setStatusCode(HttpStatus.FOUND);
                                response.getHeaders().setLocation(URI.create(contextPath + "/main/items"));
                            }))
                    );
        };

        ServerAuthenticationFailureHandler failureHandler = (webFilterExchange, exception) ->
                webFilterExchange.getExchange().getSession()
                        .flatMap(session -> {
                            String message;

                            if (exception instanceof BadCredentialsException) message = "Неверный email или пароль";
                            else message = exception.getMessage();

                            session.getAttributes().put("loginError", message);
                            return Mono.empty();
                        })
                        .then(Mono.defer(() -> {
                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                            response.setStatusCode(HttpStatus.FOUND);
                            response.getHeaders().setLocation(URI.create(contextPath + "/login"));
                            return response.setComplete();
                        }));

        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/main/items/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/main/items/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/items/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/items/**").authenticated()
                        .pathMatchers("/cart/items/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/buy").authenticated()
                        .pathMatchers(HttpMethod.GET, "/orders/**").authenticated()
                        .pathMatchers(
                                "/login",
                                "/register",
                                "/images/**",
                                "/favicon.png").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(successHandler)
                        .authenticationFailureHandler(failureHandler))
                .anonymous(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(((exchange, authentication) ->
                                        exchange.getExchange().getSession()
                                                .flatMap(WebSession::invalidate)
                                                .then(Mono.fromRunnable(() -> {
                                                    ServerHttpResponse response = exchange.getExchange().getResponse();
                                                    response.getCookies().clear();

                                                    ResponseCookie expiredCookie = ResponseCookie.from("SESSION", "")
                                                            .path("/")
                                                            .maxAge(0)
                                                            .build();
                                                    response.addCookie(expiredCookie);
                                                    response.setStatusCode(HttpStatus.FOUND);
                                                    response.getHeaders().setLocation(URI.create(contextPath + "/login"));
                                                }))
                                )
                        )
                )
                .build();
    }
}

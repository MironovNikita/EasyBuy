package com.shop.easybuy.common.security;

import com.shop.easybuy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final SecureBase64Converter secureBase64Converter;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        String loweredEmail = email.toLowerCase();

        return userRepository.findUserByEmail(secureBase64Converter.encrypt(loweredEmail))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Пользователь %s не найден".formatted(email))))
                .map(user -> CustomUserDetails.builder()
                        .userId(user.getId())
                        .email(loweredEmail)
                        .password(user.getPassword())
                        .authorities(List.of())
                        .build());
    }
}

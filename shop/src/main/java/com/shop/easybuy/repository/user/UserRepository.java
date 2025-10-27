package com.shop.easybuy.repository.user;

import com.shop.easybuy.entity.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {

    @Query("""
            SELECT * FROM users u
            WHERE u.email = :email
            """)
    Mono<User> findUserByEmail(String email);
}

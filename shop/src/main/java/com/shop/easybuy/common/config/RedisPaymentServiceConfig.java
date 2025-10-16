package com.shop.easybuy.common.config;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.invoker.payment.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
@Profile("!test")
public class RedisPaymentServiceConfig {

    @Value("${base.client.redis.url}")
    private String baseRedisUrl;

    @Bean
    public ApiClient paymentApiClient() {
        return new ApiClient()
                .setBasePath(baseRedisUrl);
    }

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi(paymentApiClient());
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(500))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }
}

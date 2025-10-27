package com.shop.easybuy.common.config;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.invoker.payment.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class RedisPaymentServiceConfig {

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${base.client.redis.url}")
    private String baseRedisUrl;

    @Value("${oauth2.default.registration.id}")
    private String defaultClientRegistrationId;

    @Bean
    public ApiClient paymentApiClient() {
        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId(defaultClientRegistrationId);

        var webClient = WebClient.builder()
                .baseUrl(baseRedisUrl)
                .filter(oauth2)
                .build();

        return new ApiClient(webClient)
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

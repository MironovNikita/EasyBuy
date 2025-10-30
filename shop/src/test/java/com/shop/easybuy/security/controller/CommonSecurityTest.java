package com.shop.easybuy.security.controller;

import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.integrationSettings.PaymentServiceMockConfig;
import com.shop.easybuy.repository.user.UserRepository;
import com.shop.easybuy.security.TestSecurityConfig;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.service.item.ItemService;
import com.shop.easybuy.service.order.OrderService;
import com.shop.easybuy.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import({TestSecurityConfig.class, PaymentServiceMockConfig.class})
public abstract class CommonSecurityTest {

    @Autowired
    protected WebTestClient webClient;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected SecurityUserContextHandler securityUserContextHandler;

    @MockitoBean
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    protected ReactiveOAuth2AuthorizedClientService authorizedClientService;
}

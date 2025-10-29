package com.shop.easybuy.controller.user;

import com.shop.easybuy.common.validation.Create;
import com.shop.easybuy.entity.user.UserCreateDto;
import com.shop.easybuy.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Validated
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public Mono<String> loginPage(
            ServerWebExchange exchange, Model model) {

        return exchange.getSession()
                .flatMap(session -> {

                    String error = session.getAttribute("loginError");
                    if (error != null) {
                        model.addAttribute("errorMessage", error);
                        session.getAttributes().remove("loginError");
                    }

                    Boolean registered = session.getAttribute("registered");
                    if (Boolean.TRUE.equals(registered)) {
                        model.addAttribute("registered", true);
                        session.getAttributes().remove("registered");
                    }

                    return Mono.just("login");
                });
    }

    @GetMapping("/register")
    public Mono<String> registerPage() {
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> register(@Validated(Create.class) @ModelAttribute UserCreateDto userCreateDto,
                                 ServerWebExchange exchange) {
        return userService.register(userCreateDto)
                .then(Mono.defer(() ->
                        exchange.getSession()
                                .flatMap(session -> {
                                    session.getAttributes().put("registered", true);
                                    return Mono.just("redirect:/login");
                                })
                ));
    }
}

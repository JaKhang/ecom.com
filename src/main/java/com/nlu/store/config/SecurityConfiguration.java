package com.nlu.store.config;

import com.nlu.store.core.security.HttpSecurity;
import com.nlu.store.core.security.MvcSecurityErrorHandler;
import com.nlu.store.core.security.SecurityConfigurer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SecurityConfiguration implements SecurityConfigurer {

    @Inject
    private MvcSecurityErrorHandler mvcSecurityErrorHandler;

    @Override
    public void configure(HttpSecurity http) {
        http
                .stateful()
                .exceptionHandling(mvcSecurityErrorHandler)
                .anonymous("/login", "/register", "/forgot-password") // Alias cho permitAll
                .hasAuthority("ROLE_ADMIN", "/admin/**")
                .authenticated("/checkout", "/thanh-toan", "/verify-pending", "/account", "/tai-khoan")
                .anyRequest()
                .permitAll();

    }
}

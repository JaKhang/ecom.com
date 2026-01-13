package com.nlu.store.modules.user.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nlu.store.core.jawire.Component;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.user.services.AuthService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
public class ResendVerifyComponent extends Component {

    @Inject
    @JsonIgnore
    private AuthService authService;

    @Getter
    @Setter
    private long countdown;

    public void resend(){
        Authentication authentication = getHttpContext().authentication();
        authService.requestVerify(authentication.identifier());
        this.countdown = authService.getRequestVerifyCountDown(authentication.identifier());

    }

    @Override
    public void mount() {
        Authentication authentication = getHttpContext().authentication();
        countdown = authService.getRequestVerifyCountDown(authentication.identifier());
    }

    @Override
    public String view() {
        return "verify-pending/resend-button";
    }



}

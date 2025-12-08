package com.nlu.store.modules.user.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor // Cần thiết cho Jackson/JSON parsing và JPA
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String fullName;
    private String password;
    private String confirmPassword;
    private Boolean agree;
}


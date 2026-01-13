package com.nlu.store.modules.user.validator;

import com.nlu.store.core.validation.SimpleValidateResult;
import com.nlu.store.core.validation.ValidateHelper;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.user.dto.LoginRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequestValidator implements Validator<LoginRequest> {

    @Override
    public ValidateResult validate(LoginRequest request) {
        // 1. Fail-fast: Kiểm tra null object trước
        if (request == null) {
            SimpleValidateResult result = new SimpleValidateResult(LoginRequest.class);
            result.addError("request", "validation.request.null");
            return result;
        }
//        Map<String, String> errors = new HashMap<>();
//        if (request.getEmail() == null)
//                errors.put("email", "validation.auth.email.required");
//        if (request.getPassword() == null)
//            errors.put("password", "sjdkfasdkfsa");
//        return new SimpleValidateResult(errors, LoginRequest.class);

//        // 2. Fluent Validation
        return ValidateHelper.of(request)
                // --- Email Validation ---
                .notBlank("email", "validation.auth.email.required")
                .isEmail("email", "validation.auth.email.invalid_format")

                // --- Password Validation ---
                .notBlank("password", "validation.auth.password.required")
                 .minLength("password", 6, "validation.auth.password.min_length")

                .validate();
    }
}

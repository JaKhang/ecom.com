package com.nlu.store.modules.user.validator;

import com.nlu.store.core.validation.SimpleValidateResult;
import com.nlu.store.core.validation.ValidateHelper;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.user.dto.LoginRequest;

public class LoginRequestValidator implements Validator<LoginRequest> {

    @Override
    public ValidateResult validate(LoginRequest request) {
        // 1. Fail-fast: Kiểm tra null object trước
        if (request == null) {
            SimpleValidateResult result = new SimpleValidateResult(LoginRequest.class);
            result.addError("request", "validate.request.null");
            return result;
        }

        // 2. Fluent Validation
        return ValidateHelper.of(request)
                // --- Email Validation ---
                .notBlank("email", "validate.auth.email.required")
                .isEmail("email", "validate.auth.email.invalid_format")

                // --- Password Validation ---
                .notBlank("password", "validate.auth.password.required")
                 .minLength("password", 6, "validate.auth.password.min_length")

                .validate();
    }
}

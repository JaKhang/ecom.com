package com.nlu.store.modules.user.validator;

import com.nlu.store.core.validation.SimpleValidateResult;
import com.nlu.store.core.validation.ValidateHelper;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.user.dto.RegisterRequest;

import java.util.Map;

public class RegisterRequestValidator implements Validator<RegisterRequest> {

    @Override
    public ValidateResult validate(RegisterRequest request) {

        if (request == null) {
            SimpleValidateResult result = new SimpleValidateResult(RegisterRequest.class);
            result.addError("request", "validate.request.null");
            return result;
        }

        return ValidateHelper.of(request)
                // 1. Email
                .notBlank("email", "validate.auth.email.required")
                .maxLength("email", 255, "validate.auth.email.max_length")
                .isEmail("email", "validate.auth.email.invalid_format")

                // 2. FullName
                .notBlank("fullName", "validate.auth.fullname.required")
                .maxLength("fullName", 100, "validate.auth.fullname.max_length")

                // 3. Password
                .notBlank("password", "validate.auth.password.required")
                .minLength("password", 6, "validate.auth.password.min_length")
                .maxLength("password", 64, "validate.auth.password.max_length")

                // 4. Confirm Password
                .notBlank("confirmPassword", "validate.auth.confirm_password.required")
                // Custom check: so sánh giá trị confirmPassword (val) với request.password()
                .check("confirmPassword",
                        val -> val != null && val.equals(request.getConfirmPassword()),
                        "validate.auth.password.not_match")
                .check("agree", Boolean.TRUE::equals, "validate.auth.agree.must_check")

                .validate();
    }


    // Helper để in kết quả cho đẹp
    private static void printResult(ValidateResult result) {
        if (!result.hasError()) {
            System.out.println("✅ VALIDATION PASSED");
        } else {
            System.out.println("❌ VALIDATION FAILED");
            for (Map.Entry<String, String> entry : result.details().entrySet()) {
                System.out.printf("   - Field: %-15s | Error: %s%n", entry.getKey(), entry.getValue());
            }
        }
    }
}

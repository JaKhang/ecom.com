package com.nlu.store.modules.order.validators;

import com.nlu.store.core.validation.ValidateHelper;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.order.dto.CheckoutRequest;

public class CheckoutValidator implements Validator<CheckoutRequest> {

    private static final String PHONE_REGEX = "^0\\d{9}$";


    @Override
    public ValidateResult validate(CheckoutRequest request) {
        return ValidateHelper.of(request)
                // Names
                .notBlank("fullName", "validation.checkout.fullname.required")

                // Phone
                .notBlank("phoneNumber", "validation.required")
                .pattern("phoneNumber", PHONE_REGEX, "validation.phone")

                // Email
                .notBlank("email", "validation.required")
                .isEmail("email", "validation.email")

                // Address (Reusing the common 'required' key)
                .notBlank("province", "validation.required")
                .notBlank("district", "validation.required")
                .notBlank("ward", "validation.required")
                .notBlank("addressDetail", "validation.required")

                // Payment
                .notNull("paymentMethod", "validation.checkout.payment.required")

                .validate();
    }
}

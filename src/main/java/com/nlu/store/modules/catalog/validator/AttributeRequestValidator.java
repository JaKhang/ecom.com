package com.nlu.store.modules.catalog.validator;

import com.nlu.store.core.validation.SimpleValidateResult;
import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import com.nlu.store.modules.catalog.dto.AttributeRequest;

import java.util.regex.Pattern;

public class AttributeRequestValidator implements Validator<AttributeRequest> {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    public ValidateResult validate(AttributeRequest request) {
        SimpleValidateResult result = new SimpleValidateResult(AttributeRequest.class);

        if (request == null) {
            result.addError("request", "validate.request.null");
            return result;
        }

        // 1. Validate Name
        if (request.name() == null || request.name().trim().isEmpty()) {
            result.addError("name", "validate.attribute.name.required");
        } else if (request.name().length() > 255) {
            result.addError("name", "validate.attribute.name.max_length");
        }

        // 2. Validate Code
        if (request.code() == null || request.code().trim().isEmpty()) {
            result.addError("code", "validate.attribute.code.required");
        } else if (request.code().length() > 50) {
            result.addError("code", "validate.attribute.code.max_length");
        } else if (!CODE_PATTERN.matcher(request.code()).matches()) {
            result.addError("code", "validate.attribute.code.invalid_format");
        }


        // 4. Validate Unit (Optional nhưng check độ dài)
        if (request.unit() != null && request.unit().length() > 20) {
            result.addError("unit", "validate.attribute.unit.max_length");
        }

        return result;
    }
}


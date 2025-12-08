package com.nlu.store.core.web.bind;

import com.nlu.store.core.validation.ValidateResult;
import com.nlu.store.core.validation.Validator;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Collections;

public class ServletDataBinder implements DataBinder {

    private final BodyParser bodyParser;

    public ServletDataBinder(BodyParser bodyParser) {
        this.bodyParser = bodyParser;
    }

    /**
     * Bind dữ liệu và thực hiện Validate
     */
    @Override
    public <T> BindingResult<T> bind(HttpServletRequest request, Class<T> tClass, Validator<T> validator) {
        // 1. Parse dữ liệu từ Request (JSON body hoặc Form params)
        T data = null;
        try {
            data = bodyParser.parse(request, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 2. Thực hiện Validate
        ValidateResult validateResult = validator.validate(data);

        // 3. Đóng gói kết quả
        return new BindingResultImpl<>(
                data,
                validateResult.hasError(),
                validateResult.details()
        );
    }

    /**
     * Chỉ Bind dữ liệu, không Validate (mặc định không có lỗi)
     */
    @Override
    public <T> BindingResult<T> bind(HttpServletRequest request, Class<T> tClass) {
        // 1. Parse dữ liệu
        T data = null;
        try {
            data = bodyParser.parse(request, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BindingResultImpl<>(data, false, Collections.emptyMap());
    }


}

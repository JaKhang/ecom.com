package com.nlu.store.core.web.bind;

import com.nlu.store.core.validation.Validator;
import jakarta.servlet.http.HttpServletRequest;

public interface DataBinder {
    <T> BindingResult<T> bind(HttpServletRequest request, Class<T> tClass, Validator<T> validator);
    <T> BindingResult<T> bind(HttpServletRequest request, Class<T> tClass);
}


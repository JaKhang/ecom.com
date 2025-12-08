package com.nlu.store.core.validation;

import java.util.Collections;
import java.util.Map;

public interface ValidateResult {


    boolean hasError();
    Map<String, String> details();
    Class<?> getValidatedClass();

    ValidateResult merge(ValidateResult other);

}

package com.nlu.store.core.web.bind;

import java.util.Map;

public interface BindingResult<T> {
    T data();
    boolean hasError();
    Map<String, String> details();
}

package com.nlu.store.core.web;

import com.nlu.store.core.data.ULID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Authentication extends Serializable {
    ULID id();

    String username();

    boolean isVerified();

    boolean isActive();

    List<String> authorities();

    Map<String, String> info();
}

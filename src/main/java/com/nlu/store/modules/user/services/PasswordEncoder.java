package com.nlu.store.modules.user.services;

public interface PasswordEncoder {
    String encode(String raw);

    boolean matches(String raw, String hashed);
}

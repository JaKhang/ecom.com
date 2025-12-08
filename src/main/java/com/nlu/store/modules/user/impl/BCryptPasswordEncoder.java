package com.nlu.store.modules.user.impl;

import com.nlu.store.modules.user.services.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String raw) {
        // Generate a salt and hash the raw password
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String raw, String hashed) {
        // Check if the raw password matches the hashed password
        return BCrypt.checkpw(raw, hashed);
    }

    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("12345678", BCrypt.gensalt()));
    }
}

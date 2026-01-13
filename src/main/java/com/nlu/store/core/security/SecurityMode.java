package com.nlu.store.core.security;


public enum SecurityMode {
    STATEFUL,  // Dùng Session (Mặc định cho MVC)
    STATELESS  // Dùng Token (Mặc định cho API/Mobile)
}


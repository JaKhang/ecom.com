package com.nlu.store.modules.user.dao;

import com.nlu.store.modules.user.models.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
    boolean existsByEmail(String email);
    void create(User user);
    void update(User user);
}

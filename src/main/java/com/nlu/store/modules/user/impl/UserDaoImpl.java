package com.nlu.store.modules.user.impl;

import com.nlu.store.core.dao.JdbcOperations;


import com.nlu.store.core.dao.ResultSetExtractor;
import com.nlu.store.modules.user.dao.RoleMapper;
import com.nlu.store.modules.user.dao.UserDao;
import com.nlu.store.modules.user.dao.UsersExtractor;
import com.nlu.store.modules.user.models.Role;
import com.nlu.store.modules.user.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserDaoImpl implements UserDao {

    private final JdbcOperations jdbc;

    // =================================================================================
    // SQL QUERIES (WITH PREFIX & JOIN)
    // =================================================================================

    // Lưu ý: Ta dùng LEFT JOIN để vẫn lấy được User ngay cả khi họ chưa có Role nào.
    // Ta alias các cột của Role (r.id -> r_id) để tránh trùng tên với cột id của User.
    // Đã thêm prefix 'u_' cho các cột của bảng users để tránh trùng lặp
    private static final String SQL_SELECT_USER_WITH_ROLES = "SELECT u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.full_name AS u_full_name, u.is_active AS u_is_active, u.verify_token AS u_verify_token, u.verify_token_expired_at AS u_verify_token_expired_at, u.verified_at AS u_verified_at, u.reset_password_token AS u_reset_password_token, u.reset_password_token_expired_at AS u_reset_password_token_expired_at, u.avatar AS u_avatar, u.created_at AS u_created_at, u.updated_at AS u_updated_at, u.deleted_at AS u_deleted_at, r.id AS r_id, r.code AS r_code, r.name AS r_name,  r.created_at AS r_created_at, r.updated_at AS r_updated_at FROM users u LEFT JOIN users_roles ur ON u.id = ur.user_id LEFT JOIN roles r ON ur.role_id = r.id ";

    private static final String SQL_FIND_BY_EMAIL = SQL_SELECT_USER_WITH_ROLES + "WHERE u.email = ?";

    private static final String SQL_FIND_BY_RESET_TOKEN = SQL_SELECT_USER_WITH_ROLES + "WHERE u.reset_password_token = ?";


    private static final String SQL_INSERT =
            "INSERT INTO users (id, email, password_hash, full_name, is_active, " +
                    "verify_token, verify_token_expired_at, created_at, updated_at, deleted_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE users SET password_hash = ?, full_name = ?, is_active = ?, " +
                    "verified_at = ?, verify_token = ?, verify_token_expired_at = ?, " +
                    "reset_password_token = ?, reset_password_token_expired_at = ?, " +
                    "avatar = ?, updated_at = ?, deleted_at = ? WHERE id = ?";


    private static final String SQL_COUNT_BY_EMAIL =
            "SELECT COUNT(1) FROM users u WHERE u.email = ?";

    private static final String SQL_INSERT_USER_ROLE =
            "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";

    private static final String SQL_DELETE_USER_ROLES =
            "DELETE FROM users_roles WHERE user_id = ?";
    
    
    private final ResultSetExtractor<List<User>> userWithRolesExtractor = new UsersExtractor(new RoleMapper("r_"), "u_");

    @Inject
    public UserDaoImpl(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    // =================================================================================
    // IMPLEMENTATION
    // =================================================================================

    @Override
    public Optional<User> findByEmail(String email) {
        // Sử dụng executeQuery với Extractor thay vì queryForObject
        return jdbc.executeQuery(SQL_FIND_BY_EMAIL, userWithRolesExtractor, email).orElse(Collections.emptyList()).stream().findAny();
    }

    @Override
    public Optional<User> findByResetToken(String token) {
        return jdbc.executeQuery(SQL_FIND_BY_RESET_TOKEN, userWithRolesExtractor, token).orElse(Collections.emptyList()).stream().findAny();
    }

    @Override
    public boolean existsByEmail(String email) {
        int count = jdbc.count(SQL_COUNT_BY_EMAIL, email);
        return count > 0;
    }

    @Override
    public void create(User user) {
        jdbc.executeTransaction(conn -> {
            // 1. Insert thông tin User
            jdbc.update(conn, SQL_INSERT,
                    user.getId().toString(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFullName(),
                    user.isActive(),
                    user.getVerifyToken(),
                    toTimestamp(user.getVerifyTokenExpiredAt()),
                    toTimestamp(user.getCreatedAt()),
                    toTimestamp(user.getUpdatedAt()),
                    toTimestamp(user.getDeletedAt())
            );

            // 2. Insert Roles (Nếu có)
            insertRoles(conn, user);

            return null; // Callback yêu cầu return
        });
    }

    @Override
    public void update(User user) {
        jdbc.executeTransaction(conn -> {
            // 1. Update thông tin User
            jdbc.update(conn, SQL_UPDATE,
                    user.getPasswordHash(),
                    user.getFullName(),
                    user.isActive(),
                    toTimestamp(user.getVerifiedAt()),
                    user.getVerifyToken(),
                    toTimestamp(user.getVerifyTokenExpiredAt()),
                    user.getResetPasswordToken(),
                    toTimestamp(user.getResetPasswordTokenExpiredAt()),
                    user.getAvatar(),
                    toTimestamp(user.getUpdatedAt()),
                    toTimestamp(user.getDeletedAt()),
                    // WHERE ID
                    user.getId().toString()
            );

            // 2. Cập nhật Roles
            // Logic: Xóa hết role cũ -> Insert role mới
            // Chỉ thực hiện nếu list roles không null (null nghĩa là không muốn update phần quyền)
            if (user.getRoles() != null) {
                // Xóa roles cũ
                jdbc.update(conn, SQL_DELETE_USER_ROLES, user.getId());

                // Insert roles mới
                insertRoles(conn, user);
            }

            return true;
        });
    }

    // =================================================================================
    // HELPER PRIVATE METHOD
    // =================================================================================

    private void insertRoles(Connection conn, User user) throws SQLException {
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (Role role : user.getRoles()) {
                // Giả sử Role có ID là Long
                batchArgs.add(new Object[]{
                        user.getId().toString(),
                        role.getId()
                });
            }

            // Dùng executeBatch để insert nhanh nhiều dòng
            jdbc.executeBatch(conn, SQL_INSERT_USER_ROLE, batchArgs);
        }
    }


    // =================================================================================
    // HELPER METHODS
    // =================================================================================

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}


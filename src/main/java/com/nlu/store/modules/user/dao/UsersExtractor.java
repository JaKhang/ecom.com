package com.nlu.store.modules.user.dao;

import com.nlu.store.core.dao.ResultSetExtractor;
import com.nlu.store.core.dao.ResultSetReader;
import com.nlu.store.core.dao.RowMapper;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.user.models.User;
import com.nlu.store.modules.user.models.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsersExtractor implements ResultSetExtractor<List<User>> {

    private final RowMapper<Role> roleMapper;
    private final String userPrefix;
    private static final String DEFAULT_PREFIX = "user_";


    /**
     * Constructor mặc định (không dùng prefix hoặc prefix mặc định)
     */
    public UsersExtractor(RowMapper<Role> roleMapper) {
        this(roleMapper, DEFAULT_PREFIX);
    }

    /**
     * Constructor hỗ trợ tùy chỉnh prefix
     *
     * @param roleMapper Mapper để map dữ liệu Role
     * @param userPrefix Tiền tố cho các cột của User (ví dụ: "u_")
     */
    public UsersExtractor(RowMapper<Role> roleMapper, String userPrefix) {
        this.roleMapper = roleMapper;
        this.userPrefix = userPrefix == null ? DEFAULT_PREFIX : userPrefix;
    }

    @Override
    public List<User> extractData(ResultSetReader reader) throws SQLException {
        Map<ULID, User> userMap = new LinkedHashMap<>();
        int rowNum = 0;

        while (reader.next()) {
            // 1. Lấy ID User với prefix (ví dụ: "u_id")
            ULID userId = reader.getULID(userPrefix + "id");

            User user = userMap.get(userId);

            if (user == null) {
                // 2. Map các trường của User kèm theo prefix
                user = User.builder()
                        .id(userId)
                        .createdAt(reader.getLocalDateTime(userPrefix + "created_at"))
                        .updatedAt(reader.getLocalDateTime(userPrefix + "updated_at"))
                        .email(reader.getString(userPrefix + "email"))
                        .passwordHash(reader.getString(userPrefix + "password_hash"))
                        .fullName(reader.getString(userPrefix + "full_name"))
                        .isActive(reader.getBoolean(userPrefix + "is_active"))
                        .deletedAt(reader.getLocalDateTime(userPrefix + "deleted_at"))
                        .verifiedAt(reader.getLocalDateTime(userPrefix + "verified_at"))
                        .verifyToken(reader.getString(userPrefix + "verify_token"))
                        .verifyTokenExpiredAt(reader.getLocalDateTime(userPrefix + "verify_token_expired_at"))
                        .resetPasswordToken(reader.getString(userPrefix + "reset_password_token"))
                        .resetPasswordTokenExpiredAt(reader.getLocalDateTime(userPrefix + "reset_password_token_expired_at"))
                        .roles(new ArrayList<>()) // Khởi tạo list rỗng
                        .build();

                userMap.put(userId, user);
            }

            // 3. Kiểm tra xem dòng này có Role không
            // Dùng roleIdColumnLabel được cấu hình (ví dụ: "r_id") để kiểm tra
            ULID roleId = reader.getULID(roleMapper.getPrefix() + "id");

            if (roleId != null) {
                // Gọi roleMapper. Lưu ý: roleMapper cần tự biết cách đọc cột của nó
                // (ví dụ: roleMapper cũng phải được cấu hình prefix "r_" bên trong nó nếu cần)
                Role role = roleMapper.mapRow(reader, rowNum);

                if (role != null && !user.getRoles().contains(role)) {
                    user.getRoles().add(role);
                }
            }
            rowNum++;
        }

        return new ArrayList<>(userMap.values());
    }
}


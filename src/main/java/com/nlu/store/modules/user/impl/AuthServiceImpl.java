package com.nlu.store.modules.user.impl;

import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.notification.EmailService;
import com.nlu.store.modules.user.*;
import com.nlu.store.modules.user.dao.UserDao;
import com.nlu.store.modules.user.dto.LoginRequest;
import com.nlu.store.modules.user.dto.RegisterRequest;
import com.nlu.store.modules.user.models.AuthPrincipal;
import com.nlu.store.modules.user.models.User;
import com.nlu.store.modules.user.services.AuthService;
import com.nlu.store.modules.user.services.PasswordEncoder;
import com.nlu.store.modules.user.services.TokenGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Collections;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final long verifyTokenAge;

    @Inject
    public AuthServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, EmailService emailService, PropertySource config) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.verifyTokenAge = config.getLong("auth.verify-token.age", 30);
    }

    @Override
    public Authentication login(LoginRequest request) {
        // 1. Tìm User từ Database
        // Lưu ý: Không throw exception ngay nếu không tìm thấy để tránh lộ thông tin email
        User user = userDao.findByEmail(request.getEmail())
                .orElse(null);

        // 2. Kiểm tra Credential (Tài khoản & Mật khẩu)
        // BẢO MẬT: Kết hợp kiểm tra (user == null) VÀ (password không khớp) trong cùng một khối.
        // Điều này giúp ngăn chặn kẻ tấn công đoán được email nào đã tồn tại trong hệ thống
        // (User Enumeration Attack) dựa trên thông báo lỗi hoặc thời gian phản hồi.
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialException("auth.login.failed");
        }

        // 3. Kiểm tra trạng thái: Đã bị xóa (Soft Delete)
        // Nếu user có trường deleted_at khác null, coi như tài khoản không còn tồn tại
        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        // 4. Kiểm tra trạng thái: Đang bị khóa (Inactive)
        // isActive = false nghĩa là admin đã khóa hoặc user chưa kích hoạt
        if (!user.isActive()) {
            throw new AuthenticationException("auth.account.locked");
        }

        // 5. (Tùy chọn) Kiểm tra xác thực Email
        // Nếu nghiệp vụ yêu cầu bắt buộc phải xác thực email mới cho login:
        /*
        if (user.getVerifiedAt() == null) {
            throw new AuthenticationException("auth.account.not_verified");
        }
        */

        // 6. Đăng nhập thành công
        // Tạo đối tượng AuthPrincipal chứa thông tin user (đã bao gồm Roles do DAO xử lý)
        return new AuthPrincipal(user);
    }

    @Override
    public void register(RegisterRequest request) {
        // 1. Kiểm tra Email đã tồn tại chưa
        if (userDao.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("auth.register.email_exists");
        }

        // 2. Chuẩn bị dữ liệu
        LocalDateTime now = LocalDateTime.now();

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Khởi tạo User mới
        User newUser = User.builder()
                .id(ULID.fast())                 // Tạo ID mới (Primary Key)
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .fullName(request.getFullName())
                .isActive(true)                // Mặc định false, chờ xác thực email
                .verifyTokenExpiredAt(null) // Token có hiệu lực 24h
                .createdAt(now)
                .updatedAt(now)

                // Các trường null/mặc định
                .verifyToken(null)
                .deletedAt(null)
                .verifiedAt(null)
                .resetPasswordToken(null)
                .resetPasswordTokenExpiredAt(null)
                .avatar(null)                   // Có thể set avatar mặc định ở đây nếu muốn
                .roles(Collections.emptyList()) // Khởi tạo list role rỗng
                .build();

        // 4. Lưu xuống Database
        // Lưu ý: Trong UserDao.save(), bạn nên có logic để gán Role mặc định (ví dụ: ROLE_USER)
        // hoặc chèn vào bảng users_roles ngay sau khi insert user.
        userDao.create(newUser);


    }


    @Override
    public void verify(String email, String token) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("auth.user.not_found"));

        if (user.getVerifiedAt() != null) {
            throw new AlreadyVerifiedException("auth.verify.already_verified");
        }

        if (!token.equals(user.getVerifyToken())) {
            throw new InvalidVerifyTokenException("auth.verify.invalid_token");
        }

        if (user.getVerifyTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredVerifyTokenException("auth.verify.token_expired");
        }

        user.setVerifiedAt(LocalDateTime.now());
        user.setVerifyToken(null);
        user.setVerifyTokenExpiredAt(null);
        userDao.update(user);
    }

    @Override
    public void requestVerify(String email) {
        // 1. Tìm user
        // Key: auth.user.not_found
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("auth.user.not_found"));

        // 2. Kiểm tra tài khoản bị xóa
        // Key: auth.account.deleted
        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        // 3. Kiểm tra đã xác thực chưa
        // Key: auth.verify.already_verified
        if (user.getVerifiedAt() != null) {
            throw new AuthenticationException("auth.verify.already_verified");
        }

        // 3. Tạo token mới
        String newToken = tokenGenerator.generate(64);
        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(verifyTokenAge); // Gia hạn thêm 24h

        // 4. Cập nhật thông tin User
        user.setVerifyToken(newToken);
        user.setVerifyTokenExpiredAt(newExpiry);

        // 5. Lưu xuống DB
        userDao.update(user);

        // 6. Gửi Email (Tích hợp EmailService tại đây)
        emailService.sendVerifyToken(user.getEmail(), newToken, verifyTokenAge);
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        // 1. Tìm user bằng token reset
        // Key: auth.reset_password.invalid_token
        User user = userDao.findByResetToken(token)
                .orElseThrow(() -> new AuthenticationException("auth.reset_password.invalid_token"));

        // 2. Kiểm tra thời gian hết hạn của token
        // Key: auth.reset_password.token_expired
        if (user.getResetPasswordTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("auth.reset_password.token_expired");
        }

        // 3. Cập nhật mật khẩu mới (QUAN TRỌNG: Phải mã hóa)
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        // 4. Xóa token để đảm bảo tính năng "One-time use" (Dùng 1 lần)
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiredAt(null);


        // 5. Lưu thay đổi xuống Database
        userDao.update(user);
    }

    @Override
    public void requestResetPassword(String email) {
        // 1. Tìm user trong hệ thống
        // Key: auth.user.not_found
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("auth.user.not_found"));

        // 2. Kiểm tra tài khoản bị xóa
        // Key: auth.account.deleted
        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        // 3. Kiểm tra tài khoản chưa kích hoạt (Tùy chọn logic nghiệp vụ)
        // Nếu tài khoản chưa kích hoạt (chưa verify email), thường sẽ không cho reset pass
        // Key: auth.account.locked
        if (!user.isActive()) {
            throw new AuthenticationException("auth.account.locked");
        }

        // 4. Tạo token reset mới
        String resetToken = tokenGenerator.generate(64);

        // 5. Cập nhật thông tin User
        user.setResetPasswordToken(resetToken);
        // Token reset mật khẩu thường chỉ nên sống ngắn (ví dụ: 15 phút)
        user.setResetPasswordTokenExpiredAt(LocalDateTime.now().plusMinutes(15));

        // 6. Lưu xuống DB
        userDao.update(user);

        // 7. Gửi Email (TODO)
        emailService.sendResetPassword(user.getEmail(), resetToken, verifyTokenAge);
    }


}


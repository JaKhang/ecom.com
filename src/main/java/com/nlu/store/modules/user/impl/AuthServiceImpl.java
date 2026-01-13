package com.nlu.store.modules.user.impl;

import com.nlu.store.core.cache.Cache;
import com.nlu.store.core.config.PropertySource;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.exceptions.AuthenticationException;
import com.nlu.store.core.web.Authentication;
import com.nlu.store.modules.notification.EmailService;
import com.nlu.store.modules.user.*;
import com.nlu.store.modules.user.dao.UserDao;
import com.nlu.store.modules.user.dto.LoginRequest;
import com.nlu.store.modules.user.dto.RegisterRequest;
import com.nlu.store.modules.user.models.AuthPrincipal;
import com.nlu.store.modules.user.models.Role;
import com.nlu.store.modules.user.models.User;
import com.nlu.store.modules.user.services.AuthService;
import com.nlu.store.modules.user.services.PasswordEncoder;
import com.nlu.store.modules.user.services.TokenGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final Duration verifyTokenAge;
    private final Cache cache;
    private final Duration verifyRequestCountDown;


    @Inject
    public AuthServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, EmailService emailService, PropertySource config, Cache cache) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.verifyTokenAge = config.getDuration("auth.verify-token.age", Duration.ofDays(1));
        this.cache = cache;
        this.verifyRequestCountDown = config.getDuration("auth.verify-token.count-down", Duration.ofMinutes(2));
    }

    @Override
    public Authentication login(LoginRequest request) {
        User user = userDao.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialException("auth.login.failed");
        }

        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("auth.account.locked");
        }

        return new AuthPrincipal(user);
    }

    @Override
    public void register(RegisterRequest request) {
        if (userDao.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("auth.register.email_exists");
        }

        LocalDateTime now = LocalDateTime.now();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .id(ULID.fast())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .fullName(request.getFullName())
                .isActive(true)
                .verifyTokenExpiredAt(null)
                .createdAt(now)
                .updatedAt(now)
                .verifyToken(null)
                .deletedAt(null)
                .verifiedAt(null)
                .resetPasswordToken(null)
                .resetPasswordTokenExpiredAt(null)
                .avatar(null)
                .roles(Collections.emptyList())
                .build();

        userDao.create(newUser);

        // Tự động gửi email xác thực ngay sau khi đăng ký (Tùy chọn logic)
        // requestVerify(newUser.getEmail());
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
    public long requestVerify(String email) {
        // 1. Kiểm tra Rate Limiting (Chống spam)
        String cacheKey = "auth:verify-req:" + email;

        // Lấy thời điểm được phép gửi tiếp theo từ Cache
        Long nextAllowedTime = cache.getLong(cacheKey);

        if (nextAllowedTime != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < nextAllowedTime) {
                // Nếu chưa đến giờ được gửi -> Trả về số giây còn lại phải chờ
                return (nextAllowedTime - currentTime) / 1000;
            }
        }

        // 2. Logic nghiệp vụ
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("auth.user.not_found"));

        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        if (user.getVerifiedAt() != null) {
            throw new AuthenticationException("auth.verify.already_verified");
        }

        String newToken = tokenGenerator.generate(64);
        LocalDateTime newExpiry = LocalDateTime.now().plus(verifyTokenAge);

        user.setVerifyToken(newToken);
        user.setVerifyTokenExpiredAt(newExpiry);
        userDao.update(user);

        // 3. Gửi Email
        emailService.sendVerifyToken(user.getEmail(), newToken, verifyTokenAge);

        // 4. Cập nhật Cache để chặn spam
        // Value: Thời điểm (timestamp) được phép gửi lần tới
        // TTL: Thời gian sống của key trong cache
        long waitTimeMillis = verifyRequestCountDown.toMillis();
        long nextTime = System.currentTimeMillis() + waitTimeMillis;

        cache.put(cacheKey, nextTime, verifyRequestCountDown.toSeconds());

        return 0; // 0 nghĩa là thành công, không cần chờ
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userDao.findByResetToken(token)
                .orElseThrow(() -> new AuthenticationException("auth.reset_password.invalid_token"));

        if (user.getResetPasswordTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("auth.reset_password.token_expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiredAt(null);

        userDao.update(user);
    }

    @Override
    public void requestResetPassword(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("auth.user.not_found"));

        if (user.getDeletedAt() != null) {
            throw new AuthenticationException("auth.account.deleted");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("auth.account.locked");
        }

        // Có thể thêm Rate Limiting tương tự requestVerify ở đây nếu muốn

        String resetToken = tokenGenerator.generate(64);

        // Token reset pass sống 15 phút
        Duration resetTokenAge = Duration.ofMinutes(15);

        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiredAt(LocalDateTime.now().plus(resetTokenAge));

        userDao.update(user);

        // Hoàn thành TODO: Gửi email
        emailService.sendResetPassword(user.getEmail(), resetToken, resetTokenAge);
    }

    @Override
    public long getRequestVerifyCountDown(String email) {
        // 1. Tạo key cache (Phải trùng khớp với key trong hàm requestVerify)
        String cacheKey = "auth:verify-req:" + email;

        // 2. Lấy mốc thời gian được phép gửi tiếp theo (Timestamp)
        Long nextAllowedTime = cache.getLong(cacheKey);

        // 3. Nếu không có trong cache -> Không bị chặn -> Trả về 0
        if (nextAllowedTime == null) {
            return 0;
        }

        // 4. Tính toán thời gian còn lại
        long currentTime = System.currentTimeMillis();

        // Nếu thời gian hiện tại đã vượt qua mốc cho phép -> Hết giờ chờ -> Trả về 0
        if (currentTime >= nextAllowedTime) {
            return 0;
        }

        // 5. Trả về số giây còn lại (làm tròn)
        return (nextAllowedTime - currentTime) / 1000;
    }


}

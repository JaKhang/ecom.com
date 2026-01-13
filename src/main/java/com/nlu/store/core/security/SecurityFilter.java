package com.nlu.store.core.security;

import com.nlu.store.core.web.Authentication;
import com.nlu.store.core.web.HttpContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@WebFilter("/*")
public class SecurityFilter extends HttpFilter {

    @Inject private SecurityConfigurer configurer;
    @Inject @Named("sessionStrategy") private AuthenticationStrategy sessionStrategy;
    @Inject @Named("tokenStrategy") private AuthenticationStrategy tokenStrategy;
    @Inject @Named("hybridErrorHandler") private SecurityErrorHandler defaultErrorHandler;

    private HttpSecurity securityRules;
    private AuthenticationStrategy activeAuthStrategy;
    private SecurityErrorHandler activeErrorHandler;

    @Override
    public void init() {
        this.securityRules = new HttpSecurity();
        if (configurer != null) configurer.configure(this.securityRules);

        // 1. Chọn Strategy
        this.activeAuthStrategy = (securityRules.getMode() == SecurityMode.STATELESS)
                ? tokenStrategy : sessionStrategy;

        // 2. Chọn Handler
        this.activeErrorHandler = (securityRules.getErrorHandler() != null)
                ? securityRules.getErrorHandler() : defaultErrorHandler;
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        // CORS Setup (Rút gọn)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) { res.setStatus(200); return; }

        String path = req.getServletPath();
        String method = req.getMethod();
        HttpSecurity.SecurityRule rule = findMatchingRule(path, method);

        // Deny All nếu không khớp rule nào
        if (rule == null) {
            activeErrorHandler.onAccessDenied(req, res);
            return;
        }

        // --- LOGIC XỬ LÝ THEO LOẠI RULE ---
        if (rule.getType() == HttpSecurity.RuleType.PUBLIC) {
            chain.doFilter(req, res);
            return;
        }

        // Các loại còn lại đều cần Authentication
        Authentication auth = activeAuthStrategy.authenticate(req);

        if (auth == null) {
            activeErrorHandler.onUnauthenticated(req, res);
            return;
        }

        if (!auth.isActive()) {
            activeErrorHandler.onAccessDenied(req, res); // Tài khoản bị khóa
            return;
        }

        // Pass nếu chỉ yêu cầu Authenticated
        if (rule.getType() == HttpSecurity.RuleType.AUTHENTICATED) {
            req.setAttribute(HttpContext.AUTHENTICATION_KEY, auth);
            chain.doFilter(req, res);
            return;
        }

        // Check quyền nếu yêu cầu Authority
        if (rule.getType() == HttpSecurity.RuleType.AUTHORITY) {
            if (checkAuthorities(auth, rule.getAuthorities())) {
                req.setAttribute(HttpContext.AUTHENTICATION_KEY, auth);
                chain.doFilter(req, res);
            } else {
                activeErrorHandler.onAccessDenied(req, res);
            }
        }
    }

    private HttpSecurity.SecurityRule findMatchingRule(String path, String methodStr) {
        for (HttpSecurity.SecurityRule rule : securityRules.getRules()) {
            boolean methodMatch = (rule.getMethod() == null) || rule.getMethod().name().equalsIgnoreCase(methodStr);
            if (!methodMatch) continue;

            // Regex đơn giản hóa AntPath
            String regex = "^" + rule.getPattern().replace("/**", ".*").replace("/*", "/[^/]*") + "$";
            if (Pattern.matches(regex, path)) return rule;
        }
        return null;
    }

    private boolean checkAuthorities(Authentication auth, List<String> required) {
        if (required == null || required.isEmpty()) return true;
        for (String req : required) {
            if (auth.authorities().contains(req)) return true;
        }
        return false;
    }
}

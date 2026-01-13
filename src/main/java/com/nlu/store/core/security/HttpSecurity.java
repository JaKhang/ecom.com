package com.nlu.store.core.security;

import com.nlu.store.core.web.HttpMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpSecurity {

    private SecurityMode mode = SecurityMode.STATEFUL; // Mặc định
    private final List<SecurityRule> rules = new ArrayList<>();
    private SecurityErrorHandler errorHandler;

    public enum RuleType {
        PUBLIC,
        AUTHENTICATED,
        AUTHORITY
    }

    // --- 1. Cấu hình Mode & Handler ---

    public HttpSecurity stateful() {
        this.mode = SecurityMode.STATEFUL;
        return this;
    }

    public HttpSecurity stateless() {
        this.mode = SecurityMode.STATELESS;
        return this;
    }

    public HttpSecurity exceptionHandling(SecurityErrorHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    // --- 2. Cấu hình Rules ---

    // a. Anonymous / Public
    public HttpSecurity anonymous(String... patterns) {
        return permitAll(patterns);
    }

    public HttpSecurity permitAll(String... patterns) {
        for (String p : patterns) rules.add(new SecurityRule(null, p, RuleType.PUBLIC, Collections.emptyList()));
        return this;
    }

    public HttpSecurity permitAll(HttpMethod method, String... patterns) {
        for (String p : patterns) rules.add(new SecurityRule(method, p, RuleType.PUBLIC, Collections.emptyList()));
        return this;
    }

    // b. Authenticated (Chỉ cần login)
    public HttpSecurity authenticated(String... patterns) {
        for (String p : patterns) rules.add(new SecurityRule(null, p, RuleType.AUTHENTICATED, Collections.emptyList()));
        return this;
    }

    public HttpSecurity authenticated(HttpMethod method, String... patterns) {
        for (String p : patterns) rules.add(new SecurityRule(method, p, RuleType.AUTHENTICATED, Collections.emptyList()));
        return this;
    }

    // c. Authority (Cần quyền)
    public HttpSecurity hasAuthority(String authority, String... patterns) {
        return hasAuthority(authority, null, patterns);
    }

    public HttpSecurity hasAuthority(String authority, HttpMethod method, String... patterns) {
        for (String p : patterns) {
            rules.add(new SecurityRule(method, p, RuleType.AUTHORITY, Collections.singletonList(authority)));
        }
        return this;
    }

    // d. AnyRequest (Catch-all)
    public AnyRequestConfigurer anyRequest() {
        return new AnyRequestConfigurer(this);
    }

    public static class AnyRequestConfigurer {
        private final HttpSecurity parent;
        public AnyRequestConfigurer(HttpSecurity parent) { this.parent = parent; }

        public HttpSecurity authenticated() {
            parent.rules.add(new SecurityRule(null, "/**", RuleType.AUTHENTICATED, Collections.emptyList()));
            return parent;
        }
        public HttpSecurity permitAll() {
            parent.rules.add(new SecurityRule(null, "/**", RuleType.PUBLIC, Collections.emptyList()));
            return parent;
        }
    }

    // --- Getters ---
    public SecurityMode getMode() { return mode; }
    public SecurityErrorHandler getErrorHandler() { return errorHandler; }
    public List<SecurityRule> getRules() { return rules; }

    // --- Inner Class Rule ---
    public static class SecurityRule {
        private final HttpMethod method;
        private final String pattern;
        private final RuleType type;
        private final List<String> authorities;

        public SecurityRule(HttpMethod method, String pattern, RuleType type, List<String> authorities) {
            this.method = method;
            this.pattern = pattern;
            this.type = type;
            this.authorities = authorities;
        }
        public HttpMethod getMethod() { return method; }
        public String getPattern() { return pattern; }
        public RuleType getType() { return type; }
        public List<String> getAuthorities() { return authorities; }
    }
}

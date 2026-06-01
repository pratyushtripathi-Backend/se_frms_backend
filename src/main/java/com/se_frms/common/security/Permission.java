package com.se_frms.common.security;

public enum Permission {

    AUTH_LOGIN("auth:login"),
    AUTH_REGISTER("auth:register"),
    PROFILE_READ_SELF("profile:read:self"),
    PROFILE_UPDATE_SELF("profile:update:self"),
    USERS_READ("users:read"),
    USERS_UPDATE("users:update"),
    USERS_BLOCK("users:block"),
    USERS_UNBLOCK("users:unblock"),
    USERS_ASSIGN_ROLE("users:assign-role"),
    DEPARTMENT_USERS_READ("department-users:read"),
    AUDIT_LOGS_READ("audit-logs:read"),
    RULES_MANAGE("rules:manage"),
    ALERTS_MANAGE("alerts:manage"),
    SECURITY_CONFIG_MANAGE("security-config:manage"),
    SYSTEM_ADMIN("system:admin");

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

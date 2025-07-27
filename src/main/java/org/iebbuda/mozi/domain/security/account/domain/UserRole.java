package org.iebbuda.mozi.domain.security.account.domain;

public enum UserRole {
    ROLE_USER("ROLE_USER","일반 사용자"),
    ROLE_ADMIN("ROLE_ADMIN","관리자"),
    ROLE_MANAGER("ROLE_MANAGER","매니저");

    private final String authority;
    private final String description;


    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}

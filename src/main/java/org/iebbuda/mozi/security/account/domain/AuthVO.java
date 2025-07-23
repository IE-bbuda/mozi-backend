package org.iebbuda.mozi.security.account.domain;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class AuthVO implements GrantedAuthority {
    private int userId;
    private String auth;

    @Override
    public String getAuthority() {
        return auth;
    }
}

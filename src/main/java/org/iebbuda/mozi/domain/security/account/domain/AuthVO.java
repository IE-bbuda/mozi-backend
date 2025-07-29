package org.iebbuda.mozi.domain.security.account.domain;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class AuthVO implements GrantedAuthority {
    private int userId;
    private String auth;

    public AuthVO(int userId, UserRole role){
        this.userId=userId;
        this.auth = role.getAuthority();
    }

    // 기본 생성자도 유지 (MyBatis용)
    public AuthVO() {}


    @Override
    public String getAuthority() {
        return auth;
    }

    //Enum으로 변환하는 메서드
    public UserRole getRole(){
        return UserRole.valueOf(auth);
    }
}

package org.iebbuda.mozi.security.account.domain;

import lombok.Getter;
import lombok.Setter;
import org.iebbuda.mozi.user.domain.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
@Setter
public class CustomUser extends User {
    private UserVO user;

    public CustomUser(String loginId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(loginId, password, authorities);
    }

    public CustomUser(UserVO vo) {
        super(vo.getLoginId(), vo.getPassword(), vo.getAuthList());
        this.user=vo;
    }
}

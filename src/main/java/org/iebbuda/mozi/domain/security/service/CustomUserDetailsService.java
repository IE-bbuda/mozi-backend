package org.iebbuda.mozi.domain.security.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.iebbuda.mozi.domain.security.account.mapper.UserDetailsMapper;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Log4j2
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsMapper mapper;
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserVO vo = mapper.get(loginId);

        if(vo==null){
            throw new UsernameNotFoundException(loginId + "은 없는 id입니다.");
        }
        return new CustomUser(vo);
    }
}

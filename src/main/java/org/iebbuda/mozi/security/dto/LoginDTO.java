package org.iebbuda.mozi.security.dto;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDTO {
    private String loginId;
    private String password;

    public static LoginDTO of(HttpServletRequest request) {
        ObjectMapper om = new ObjectMapper();
        try{
            return om.readValue(request.getInputStream(), LoginDTO.class);
        }catch (Exception e){
            e.printStackTrace();
            throw new BadCredentialsException("username 또는 password가 없습니다");
        }
    }
}

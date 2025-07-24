package org.iebbuda.mozi.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginIdFindByEmailRequestDTO {

    private String username;
    private String email;
}

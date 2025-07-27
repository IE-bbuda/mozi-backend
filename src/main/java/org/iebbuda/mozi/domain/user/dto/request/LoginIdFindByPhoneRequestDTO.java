package org.iebbuda.mozi.domain.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginIdFindByPhoneRequestDTO {

    private String username;

    private String phoneNumber;
}

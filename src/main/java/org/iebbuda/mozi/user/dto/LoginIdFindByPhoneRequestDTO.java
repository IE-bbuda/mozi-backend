package org.iebbuda.mozi.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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

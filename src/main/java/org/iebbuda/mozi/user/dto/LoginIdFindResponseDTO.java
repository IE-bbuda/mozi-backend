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
public class LoginIdFindResponseDTO {

    private boolean found;

    @JsonProperty("masked_login_id")
    private String maskedLoginId;
    private String message;

    public static LoginIdFindResponseDTO success(String maskedLoginId){
        return LoginIdFindResponseDTO.builder()
                .found(true)
                .maskedLoginId(maskedLoginId)
                .message("아이디를 찾았습니다.")
                .build();
    }

    public static LoginIdFindResponseDTO notFound(){
        return LoginIdFindResponseDTO.builder()
                .found(false)
                .maskedLoginId(null)
                .message("입력하신 정보로 가입된 아이디가 없습니다.")
                .build();
    }
}

package org.iebbuda.mozi.domain.user.dto.response;


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

    public static LoginIdFindResponseDTO success(String maskedLoginId){
        return LoginIdFindResponseDTO.builder()
                .found(true)
                .maskedLoginId(maskedLoginId)
                .build();
    }

    public static LoginIdFindResponseDTO notFound(){
        return LoginIdFindResponseDTO.builder()
                .found(false)
                .maskedLoginId(null)
                .build();
    }
}

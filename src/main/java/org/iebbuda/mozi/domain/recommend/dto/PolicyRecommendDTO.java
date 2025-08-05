package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyRecommendDTO {

    private int policyId;
    private String plcyNo;   // 정책 고유 번호
    private String plcyNm;  // 정책명
    private int score;      // 추천 점수

    // 프론트용
    public String getPolicyId() {
        return this.plcyNo;
    }


    // 추가 정보
    private String mclsfNm;
    private Integer sprtTrgtMinAge;
    private Integer sprtTrgtMaxAge;
    private String plcyKywdNm;

}


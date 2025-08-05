package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;

@Data
@AllArgsConstructor
public class PolicyRecommendDTO {
    private int policyId;
    private String plcyNo;
    private String plcyNm;
    private int score;
    private String mclsfNm;
    private Integer sprtTrgtMinAge;
    private Integer sprtTrgtMaxAge;
    private String plcyKywdNm;

    // 🔥 여기 추가!
    public static PolicyRecommendDTO from(PolicyVO p, int score) {
        return new PolicyRecommendDTO(
                p.getPolicyId(),
                p.getPlcyNo(),
                p.getPlcyNm(),
                score,
                p.getMclsfNm(),
                p.getSprtTrgtMinAge(),
                p.getSprtTrgtMaxAge(),
                p.getPlcyKywdNm()
        );
    }
}

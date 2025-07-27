package org.iebbuda.mozi.domain.policy.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolicyVO {

    private int policyId;               // PK (AUTO_INCREMENT)
    private String plcyNm;             // 정책명
    private String plcyNo;             // 정책 고유 코드
    private String plcyExplnCn;        // 정책 설명
    private String plcySprtCn;         // 상세 지원 내용
    private String zipCd;              // 지역
    private String mrgSttsCd;          // 혼인 여부 (ENUM)
    private String schoolCd;           // 학력 (ENUM)
    private String jobCd;              // 취업 상태 (ENUM)
    private String plcyMajorCd;        // 전공 분야
    private String sBizCd;             // 특화 분야
    private String aplyUrlAddr;        // 신청 URL
    private String bizPrdBgngYmd;      // 시작일 (String으로 받고 저장 시 DATE로 변환)
    private String bizPrdEndYmd;       // 종료일
    private String lclsfNm;            // 대분류
    private String mclsfNm;            // 중분류
    private String plcyKywdNm;         // 키워드
    private Integer sprtTrgtMinAge;    // 최소 나이
    private Integer sprtTrgtMaxAge;    // 최대 나이
    private String earnCndSeCd;
    private Integer earnMinAmt;
    private Integer earnMaxAmt;
    private String earnEtcCn;
}
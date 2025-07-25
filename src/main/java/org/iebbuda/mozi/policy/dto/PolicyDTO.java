package org.iebbuda.mozi.policy.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolicyDTO {
    private int policyId;
    private String plcyNm;             // 정책명
    private String plcyNo;             // 정책 고유 코드
    private String plcyExplnCn;        // 정책 설명
    private String plcySprtCn;         // 상세 지원 내용
    private String zipCd;              // 지역
    private String mrgSttsCd;          // 혼인 여부
    private String schoolCd;           // 학력
    private String jobCd;              // 취업 상태
    private String plcyMajorCd;        // 전공 분야
    private String sBizCd;             // 특화 분야
    private String aplyUrlAddr;        // 신청 URL
    private String bizPrdBgngYmd;      // 시작일
    private String bizPrdEndYmd;       // 종료일
    private String lclsfNm;            // 대분류
    private String mclsfNm;            // 중분류
    private String plcyKywdNm;         // 키워드
    private int sprtTrgtMinAge;        // 최소 연령
    private int sprtTrgtMaxAge;        // 최대 연령
    private String earnCndSeCd;        // 소득 조건 구분 코드
    private Integer earnMinAmt;        // 최소 소득
    private Integer earnMaxAmt;        // 최대 소득
    private String earnEtcCn;          // 소득 기타 내용
}

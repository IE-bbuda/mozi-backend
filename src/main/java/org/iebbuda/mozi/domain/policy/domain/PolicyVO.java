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
    private String sbizCd;             // 특화 분야
    private String aplyUrlAddr;        // 신청 URL
    private String bizPrdBgngYmd;      // 시작일 (String으로 받고 저장 시 DATE로 변환)
    private String bizPrdEndYmd;       // 종료일
    private String lclsfNm;            // 대분류
    private String mclsfNm;            // 중분류
    private String plcyKywdNm;         // 키워드
    private Integer sprtTrgtMinAge;    // 최소 나이
    private Integer sprtTrgtMaxAge;    // 최대 나이
    private String earnCndSeCd;        // 소득조건
    private Integer earnMinAmt;         //최소 소득
    private Integer earnMaxAmt;         // 최대소득
    private String earnEtcCn;           //소득 기타

    private String refUrlAddr1;  // 참고 URL
    private String sprvsnInstCdNm; // 주관기관 코드명
    private String plcyAplyMthdCn; // 정책 신청방법 내용
    private String aplyYmd; //신청기간
    private String srngMthdCn; // 심사방법 내용
    private String ptcpPrpTrgtCn; // 참여제안 대상 내용
    private Integer sprtSclCnt; // 지원 규모 수

}
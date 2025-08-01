package org.iebbuda.mozi.domain.scrap.service;

import org.iebbuda.mozi.domain.policy.domain.PolicyVO;

import java.util.List;

public interface ScrapService {
    // 정책 스크랩 등록
    void scrapPolicy(int userId, int policyId);
    // 정책 스크랩 취소
    void cancelScrapPolicy(int userId, int policyId);
    // 스크랩 여부 확인
    boolean isScrapedPolicy(int userId, int policyId);
    // 사용자가 스크랩한 정책 ID 리스트 조회
    List<Integer> getScrapedPolicyIds(int userId);

    List<PolicyVO> getScrapedPolicies(int userId);

}

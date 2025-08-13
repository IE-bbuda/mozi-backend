package org.iebbuda.mozi.domain.scrap.service;

import org.iebbuda.mozi.domain.policy.domain.PolicyVO;

import java.util.List;

public interface ScrapService {

    void scrapPolicy(int userId, String plcyNo);

    void cancelScrapPolicy(int userId, String plcyNo);

    boolean isScrapedPolicy(int userId, String plcyNo);

    List<String> getScrapedPolicyNos(int userId); // <- plcyNo 리스트 반환

    List<PolicyVO> getScrapedPolicies(int userId);

    int getTotalScrapCountByUserId(int userId);

    /**
     * 탈퇴 시 모든 스크랩 삭제
     * @param userId 사용자 ID
     */
    void deleteAllScrapsByUserId(int userId);

}

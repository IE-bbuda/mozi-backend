package org.iebbuda.mozi.domain.scrab.service;

import java.util.List;

public interface ScrabService {
    // 정책 스크랩 등록
    void scrabPolicy(int userId, int policyId);
    // 정책 스크랩 취소
    void cancelScrabPolicy(int userId, int policyId);
    // 스크랩 여부 확인
    boolean isScrabbedPolicy(int userId, int policyId);
    // 사용자가 스크랩한 정책 ID 리스트 조회
    List<Integer> getScrabbedPolicyIds(int userId);

}

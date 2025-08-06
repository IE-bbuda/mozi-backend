package org.iebbuda.mozi.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.mapper.PolicyMapper;
import org.iebbuda.mozi.domain.policy.mapper.RegionCodeMapper;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.recommend.dto.GoalPolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.dto.PolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.util.PolicyScoreCalculator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PolicyRecommendServiceImpl implements PolicyRecommendService {

    private final UserProfileMapper userProfileMapper;
    private final GoalService goalService;
    private final PolicyMapper policyMapper;
    private final RegionCodeMapper regionCodeMapper;

    @Override
    public List<GoalPolicyRecommendDTO> recommendAll(int userId) {
        log.info("🔥 전체 추천 시작 - userId={}", userId);

        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        if (profile == null) return Collections.emptyList();

        List<GoalVO> goals = goalService.getGoalVOListByUserId(userId);
        if (goals.isEmpty()) return Collections.emptyList();

        List<PolicyVO> allPolicies = policyMapper.findAll();

        return goals.stream()
                .map(goal -> {
                    List<PolicyVO> validPolicies = filterValidPolicies(profile, allPolicies, userId, goal.getKeyword());
                    List<PolicyRecommendDTO> topPolicies = scorePolicies(profile, goal, validPolicies);
                    return new GoalPolicyRecommendDTO(
                            goal.getGoalId(),
                            goal.getKeyword().name(),
                            goal.getGoalName(),
                            topPolicies
                    );
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<PolicyRecommendDTO> recommend(int userId, int goalId) {
        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        GoalVO goal = goalService.getGoalById(goalId);
        if (profile == null || goal == null) return Collections.emptyList();

        List<PolicyVO> validPolicies = filterValidPolicies(profile, policyMapper.findAll(), userId, goal.getKeyword());


        return scorePolicies(profile, goal, validPolicies);
    }

    @Override
    public List<PolicyRecommendDTO> recommendByKeyword(int userId, String keywordText) {
        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        if (profile == null) return Collections.emptyList();

        GoalVO.GoalKeyword keyword;
        try {
            keyword = GoalVO.GoalKeyword.valueOf(keywordText.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 키워드 입력: {}", keywordText);
            return Collections.emptyList();
        }

        List<PolicyVO> validPolicies = policyMapper.findAll().stream()
                .filter(p -> PolicyScoreCalculator.isAvailablePolicy(p.getBizPrdEndYmd(), p.getAplyUrlAddr()))
                .collect(Collectors.toList());

        GoalVO goal = new GoalVO();
        goal.setKeyword(keyword);
        goal.setGoalName(keyword.name());

        return scorePolicies(profile, goal, validPolicies);
    }

    // ===== 내부 공통 =====

    private List<PolicyRecommendDTO> scorePolicies(UserProfileVO profile, GoalVO goal, List<PolicyVO> policies) {
        return policies.stream()
                .map(policy -> {
                    int score = PolicyScoreCalculator.calculateTotalScore(profile, goal.getKeyword(), policy, regionCodeMapper);
                    return new AbstractMap.SimpleEntry<>(policy, score);
                })
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(entry -> PolicyRecommendDTO.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // 로그용
    private List<PolicyVO> filterValidPolicies(UserProfileVO profile, List<PolicyVO> allPolicies, int userId, GoalVO.GoalKeyword keyword) {
        log.info("🟦 [추천 시작] 사용자 ID: {}, 목표 키워드: {}", userId, keyword);
        log.info("📊 전체 정책 수: {}", allPolicies.size());

        // 1. 신청 기한 유효
        List<PolicyVO> step1 = allPolicies.stream()
                .filter(p -> PolicyScoreCalculator.isAvailablePolicy(p.getAplyYmd(), p.getBizPrdEndYmd()
                        ))
                .collect(Collectors.toList());
        log.info("🔎 [1차 필터링] 신청 기한 유효 → {}건", step1.size());

        // 2. 연령 조건
        List<PolicyVO> step2 = step1.stream()
                .filter(p -> PolicyScoreCalculator.calculateAgeScore(
                        profile.getAge(), p.getSprtTrgtMinAge(), p.getSprtTrgtMaxAge()
                ) > 0)
                .collect(Collectors.toList());
        log.info("🔎 [2차 필터링] 연령 조건 통과 → {}건", step2.size());

        // 3. 연소득 조건
        List<PolicyVO> step3 = step2.stream()
                .filter(p -> PolicyScoreCalculator.calculateIncomeScore(
                        profile.getAnnualIncome(), p.getEarnCndSeCd(), p.getEarnMinAmt(), p.getEarnMaxAmt()
                ) >= 0)
                .collect(Collectors.toList());
        log.info("🔎 [3차 필터링] 연소득 조건 통과 → {}건", step3.size());

        // 4. 지역 조건
        List<PolicyVO> step4 = step3.stream()
                .filter(p -> PolicyScoreCalculator.calculateRegionScore(
                        profile.getRegion(), p.getZipCd(), regionCodeMapper
                ) > 0)
                .collect(Collectors.toList());
        log.info("🔎 [4차 필터링] 지역 조건 통과 → {}건", step4.size());

        log.info("✅ [최종 추천 후보] {} / {}건", step4.size(), allPolicies.size());

        return step4;
    }

}

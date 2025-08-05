package org.iebbuda.mozi.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.mapper.PolicyMapper;
import org.iebbuda.mozi.domain.policy.mapper.RegionCodeMapper;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.recommend.dto.GoalPolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.dto.PolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.util.PolicyKeywordMapping;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    // 전체 추천 (모든 목표 기반)
    @Override
    public List<GoalPolicyRecommendDTO> recommendAll(int userId) {
        log.info("🔥 전체 추천 시작 - userId={}", userId);

        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        if (profile == null) return Collections.emptyList();

        List<GoalVO> goals = goalService.getGoalVOListByUserId(userId);
        if (goals.isEmpty()) return Collections.emptyList();

        List<PolicyVO> allPolicies = policyMapper.findAll();
        List<PolicyVO> filtered = filterPoliciesByUserProfile(profile, allPolicies);

        return goals.stream()
                .map(goal -> {
                    List<PolicyRecommendDTO> topPolicies = scorePoliciesByKeyword(goal.getKeyword(), filtered);

                    // ✅ 순서: goalId, keyword, goalName, recommendations
                    return new GoalPolicyRecommendDTO(
                            goal.getGoalId(),
                            goal.getKeyword().name(),
                            goal.getGoalName(),
                            topPolicies
                    );
                })
                .collect(Collectors.toList());
    }

    // 개별 목표 추천
    @Override
    public List<PolicyRecommendDTO> recommend(int userId, int goalId) {
        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        GoalVO goal = goalService.getGoalById(goalId);
        if (profile == null || goal == null) return Collections.emptyList();

        List<PolicyVO> filtered = filterPoliciesByUserProfile(profile, policyMapper.findAll());
        return scorePoliciesByKeyword(goal.getKeyword(), filtered);
    }

    // 키워드 기반 추천
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

        List<PolicyVO> filtered = filterPoliciesByUserProfile(profile, policyMapper.findAll());
        return scorePoliciesByKeyword(keyword, filtered);
    }

    // ===== 공통 메서드 =====

    private List<PolicyVO> filterPoliciesByUserProfile(UserProfileVO profile, List<PolicyVO> allPolicies) {
        return allPolicies.stream()
                .filter(p -> matchEnum(profile.getMaritalStatus(), p.getMrgSttsCd()))
                .filter(p -> matchEnum(profile.getEducationLevel(), p.getSchoolCd()))
                .filter(p -> matchEnum(profile.getEmploymentStatus(), p.getJobCd()))
                .filter(p -> matchEnum(profile.getMajor(), p.getPlcyMajorCd()))
                .filter(p -> matchEnum(profile.getSpecialty(), p.getSbizCd()))
                .filter(p -> matchRegion(profile.getRegion(), p.getZipCd()))
                .filter(p -> matchAge(profile.getAge(), p.getSprtTrgtMinAge(), p.getSprtTrgtMaxAge()))
                .filter(p -> matchIncome(profile.getAnnualIncome(), p.getEarnCndSeCd(), p.getEarnMinAmt(), p.getEarnMaxAmt()))
                .collect(Collectors.toList());
    }

    private List<PolicyRecommendDTO> scorePoliciesByKeyword(GoalVO.GoalKeyword keyword, List<PolicyVO> filteredPolicies) {
        return filteredPolicies.stream()
                .map(policy -> {
                    int score = 0;
                    if (PolicyKeywordMapping.matchCategory(keyword, policy.getLclsfNm())) score += 5;
                    if (PolicyKeywordMapping.matchSubcategory(keyword, policy.getMclsfNm())) score += 3;
                    if (PolicyKeywordMapping.matchKeyword(keyword, policy.getPlcyKywdNm())) score += 2;
                    return new AbstractMap.SimpleEntry<>(policy, score);
                })
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(entry -> PolicyRecommendDTO.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // ===== 필터링 서브 메서드 =====

    private boolean matchEnum(Enum<?> userValue, String policyCode) {
        if (userValue == null || policyCode == null || policyCode.isBlank()) return true;
        String userCode = getCodeFromEnum(userValue);
        String noRestrictionCode = getNoRestrictionCode(userValue);
        List<String> policyCodes = Arrays.asList(policyCode.split(","));
        return policyCodes.contains(noRestrictionCode) || policyCodes.contains(userCode);
    }

    private boolean matchRegion(Region region, String policyZipCd) {
        if (region == null || policyZipCd == null || policyZipCd.isBlank()) return true;
        List<String> regionZipCodes = regionCodeMapper.findZipCodesBySido(region.getLabel());
        List<String> policyZipCodes = Arrays.asList(policyZipCd.split(","));
        return regionZipCodes.stream().anyMatch(policyZipCodes::contains);
    }

    private boolean matchAge(int userAge, Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) return true;
        int min = (minAge != null) ? minAge : 0;
        int max = (maxAge != null) ? maxAge : 200;
        return userAge >= min && userAge <= max;
    }

    private boolean matchIncome(BigDecimal userIncome, String earnCode, Integer minAmt, Integer maxAmt) {
        if (earnCode == null || earnCode.isBlank()) return true;
        if (earnCode.equals("0043001") || earnCode.equals("0043003")) return true;
        if (earnCode.equals("0043002")) {
            BigDecimal min = (minAmt != null) ? new BigDecimal(minAmt) : BigDecimal.ZERO;
            BigDecimal max = (maxAmt != null) ? new BigDecimal(maxAmt) : BigDecimal.valueOf(Integer.MAX_VALUE);
            return userIncome.compareTo(min) >= 0 && userIncome.compareTo(max) <= 0;
        }
        return true;
    }

    private String getCodeFromEnum(Enum<?> e) {
        switch (e.getDeclaringClass().getSimpleName()) {
            case "EducationLevel": return EducationLevel.valueOf(e.name()).getCode();
            case "EmploymentStatus": return EmploymentStatus.valueOf(e.name()).getCode();
            case "Major": return Major.valueOf(e.name()).getCode();
            case "Specialty": return Specialty.valueOf(e.name()).getCode();
            case "MaritalStatus": return MaritalStatus.valueOf(e.name()).getCode();
            default: return null;
        }
    }

    private String getNoRestrictionCode(Enum<?> e) {
        switch (e.getDeclaringClass().getSimpleName()) {
            case "EducationLevel": return EducationLevel.NO_RESTRICTION.getCode();
            case "EmploymentStatus": return EmploymentStatus.NO_RESTRICTION.getCode();
            case "Major": return Major.NO_RESTRICTION.getCode();
            case "Specialty": return Specialty.NO_RESTRICTION.getCode();
            case "MaritalStatus": return MaritalStatus.NO_RESTRICTION.getCode();
            default: return null;
        }
    }
}

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
import org.iebbuda.mozi.domain.profile.domain.enums.*;
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

    @Override
    public List<PolicyRecommendDTO> recommend(int userId, int goalId) {
        log.info(" 추천 시작 - userId={}, goalId={}", userId, goalId);

        // 1. 퍼스널 정보 조회
        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            log.warn("UserProfile not found for userId={}", userId);
            return Collections.emptyList();
        }

        // 2. 목표 정보 조회
        GoalVO goal = goalService.getGoalById(goalId);
        if (goal == null) {
            log.warn("Goal not found for goalId={}", goalId);
            return Collections.emptyList();
        }

        // 3. 정책 전체 조회
        List<PolicyVO> allPolicies = policyMapper.findAll();
        log.info("정책 전체 개수: {}", allPolicies.size());

        // 4. 퍼스널 정보 기반 필터링 (혼인, 학력, 취업, 전공, 특화)
        List<PolicyVO> filtered = allPolicies.stream()
                .filter(p -> matchEnum(profile.getMaritalStatus(), p.getMrgSttsCd()))
                .filter(p -> matchEnum(profile.getEducationLevel(), p.getSchoolCd()))
                .filter(p -> matchEnum(profile.getEmploymentStatus(), p.getJobCd()))
                .filter(p -> matchEnum(profile.getMajor(), p.getPlcyMajorCd()))
                .filter(p -> matchEnum(profile.getSpecialty(), p.getSbizCd()))
                .filter(p -> matchRegion(profile.getRegion(), p.getZipCd()))
                .filter(p -> matchAge(profile.getAge(), p.getSprtTrgtMinAge(), p.getSprtTrgtMaxAge()))
                .filter(p -> matchIncome(profile.getAnnualIncome(), p.getEarnCndSeCd(), p.getEarnMinAmt(), p.getEarnMaxAmt()))
                .collect(Collectors.toList());

        log.info("1차 필터링 후 남은 정책 수: {}", filtered.size());

        // 5. 목표 키워드 기반 점수화
        GoalVO.GoalKeyword keyword = goal.getKeyword();
        Map<PolicyVO, Integer> scored = new HashMap<>();

        for (PolicyVO policy : filtered) {
            int score = 0;
            if (PolicyKeywordMapping.matchCategory(keyword, policy.getLclsfNm())) score += 5;
            if (PolicyKeywordMapping.matchSubcategory(keyword, policy.getMclsfNm())) score += 3;
            if (PolicyKeywordMapping.matchKeyword(keyword, policy.getPlcyKywdNm())) score += 2;
            scored.put(policy, score);
        }

        // 6. 상위 5개 추출
        List<PolicyRecommendDTO> result = scored.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(entry -> {
                    PolicyVO p = entry.getKey();
                    return new PolicyRecommendDTO(
                            p.getPolicyId(),
                            p.getPlcyNo(),
                            p.getPlcyNm(),
                            entry.getValue(),
                            p.getMclsfNm(),
                            p.getSprtTrgtMinAge(),
                            p.getSprtTrgtMaxAge(),
                            p.getPlcyKywdNm()
                    );
                })
                .collect(Collectors.toList());

        log.info("추천 정책 수: {}", result.size());
        return result;
    }

    @Override
    public List<PolicyRecommendDTO> recommendByKeyword(int userId, String keywordText) {
        log.info("키워드 추천 시작 - userId={}, keyword={}", userId, keywordText);

        // 1. 퍼스널 정보 조회
        UserProfileVO profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            log.warn("UserProfile not found for userId={}", userId);
            return Collections.emptyList();
        }

        // 2. 정책 전체 조회
        List<PolicyVO> allPolicies = policyMapper.findAll();
        log.info("정책 전체 개수: {}", allPolicies.size());

        // 3. 퍼스널 정보 기반 필터링
        List<PolicyVO> filtered = allPolicies.stream()
                .filter(p -> matchEnum(profile.getMaritalStatus(), p.getMrgSttsCd()))
                .filter(p -> matchEnum(profile.getEducationLevel(), p.getSchoolCd()))
                .filter(p -> matchEnum(profile.getEmploymentStatus(), p.getJobCd()))
                .filter(p -> matchEnum(profile.getMajor(), p.getPlcyMajorCd()))
                .filter(p -> matchEnum(profile.getSpecialty(), p.getSbizCd()))
                .filter(p -> matchRegion(profile.getRegion(), p.getZipCd()))
                .filter(p -> matchAge(profile.getAge(), p.getSprtTrgtMinAge(), p.getSprtTrgtMaxAge()))
                .filter(p -> matchIncome(profile.getAnnualIncome(), p.getEarnCndSeCd(), p.getEarnMinAmt(), p.getEarnMaxAmt()))
                .collect(Collectors.toList());

        log.info("1차 필터링 후 남은 정책 수: {}", filtered.size());

        // 4. 키워드 Enum으로 변환 (GoalKeyword Enum 활용)
        GoalVO.GoalKeyword keyword;
        try {
            keyword = GoalVO.GoalKeyword.valueOf(keywordText.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 키워드 입력: {}", keywordText);
            return Collections.emptyList();
        }

        // 5. 키워드 기반 점수화
        Map<PolicyVO, Integer> scored = new HashMap<>();
        for (PolicyVO policy : filtered) {
            int score = 0;
            if (PolicyKeywordMapping.matchCategory(keyword, policy.getLclsfNm())) score += 5;
            if (PolicyKeywordMapping.matchSubcategory(keyword, policy.getMclsfNm())) score += 3;
            if (PolicyKeywordMapping.matchKeyword(keyword, policy.getPlcyKywdNm())) score += 2;
            scored.put(policy, score);
        }

        // 6. 상위 5개 추출
        List<PolicyRecommendDTO> result = scored.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(entry -> {
                    PolicyVO p = entry.getKey();
                    return new PolicyRecommendDTO(
                            p.getPolicyId(),
                            p.getPlcyNo(),
                            p.getPlcyNm(),
                            entry.getValue(),
                            p.getMclsfNm(),
                            p.getSprtTrgtMinAge(),
                            p.getSprtTrgtMaxAge(),
                            p.getPlcyKywdNm()

                    );
                })
                .collect(Collectors.toList());

        log.info("키워드 추천 결과 수: {}", result.size());
        return result;
    }



    // Enum 필드 매칭 (제한없음 포함)
    private boolean matchEnum(Enum<?> userValue, String policyCode) {
        if (userValue == null || policyCode == null || policyCode.isBlank()) return true;

        String userCode = getCodeFromEnum(userValue);
        String noRestrictionCode = getNoRestrictionCode(userValue);
        List<String> policyCodes = Arrays.asList(policyCode.split(","));

        return policyCodes.contains(noRestrictionCode) || policyCodes.contains(userCode);
    }

    private boolean matchRegion(Region region, String policyZipCd) {
        if (region == null || policyZipCd == null || policyZipCd.isBlank()) return true;

        // 시도명을 기반으로 DB에서 해당 시도에 포함된 zipCode 전체 조회
        List<String> regionZipCodes = regionCodeMapper.findZipCodesBySido(region.getLabel());

        // 정책의 zipCd 필드는 "11680,11710" 형태 → 분리
        List<String> policyZipCodes = Arrays.asList(policyZipCd.split(","));

        // 두 리스트에 겹치는 zip 코드가 있으면 해당 지역으로 매칭
        return regionZipCodes.stream().anyMatch(policyZipCodes::contains);
    }


    // 나이 조건 필터링
    private boolean matchAge(int userAge, Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) return true;
        int min = (minAge != null) ? minAge : 0;
        int max = (maxAge != null) ? maxAge : 200;
        return userAge >= min && userAge <= max;
    }

    // 연소득 조건 필터링

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

    // Enum → 정책코드
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

    // 제한없음 코드
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

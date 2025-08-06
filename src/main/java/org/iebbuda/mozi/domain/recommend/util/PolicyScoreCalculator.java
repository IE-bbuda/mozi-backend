package org.iebbuda.mozi.domain.recommend.util;

import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.mapper.RegionCodeMapper;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PolicyScoreCalculator {

    public static int calculateTotalScore(UserProfileVO profile, GoalVO.GoalKeyword keyword, PolicyVO policy, RegionCodeMapper regionCodeMapper) {
        int total = 0;
        total += calculateAgeScore(profile.getAge(), policy.getSprtTrgtMinAge(), policy.getSprtTrgtMaxAge());              // 25점
        total += calculateIncomeScore(profile.getAnnualIncome(), policy.getEarnCndSeCd(), policy.getEarnMinAmt(), policy.getEarnMaxAmt()); // 5점

        total += calculateEnumScore(profile.getMaritalStatus(), policy.getMrgSttsCd(), MaritalStatus.NO_RESTRICTION.getCode(), 6);
        total += calculateEnumScore(profile.getEducationLevel(), policy.getSchoolCd(), EducationLevel.NO_RESTRICTION.getCode(), 6);
        total += calculateEnumScore(profile.getEmploymentStatus(), policy.getJobCd(), EmploymentStatus.NO_RESTRICTION.getCode(), 6);
        total += calculateEnumScore(profile.getMajor(), policy.getPlcyMajorCd(), Major.NO_RESTRICTION.getCode(), 6);
        total += calculateEnumScore(profile.getSpecialty(), policy.getSbizCd(), Specialty.NO_RESTRICTION.getCode(), 6);

        total += calculateRegionScore(profile.getRegion(), policy.getZipCd(), regionCodeMapper);                           // 10점
        total += calculateKeywordScore(keyword, policy);                                                                   // 30점

        return total;
    }

    // ===== 1. 연령 점수 (25점) =====
    public static int calculateAgeScore(int userAge, Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) return 25;
        int min = (minAge != null) ? minAge : 0;
        int max = (maxAge != null) ? maxAge : 200;
        return (userAge >= min && userAge <= max) ? 25 : 0;
    }

    // ===== 2. 연소득 점수 (5점) =====
    public static int calculateIncomeScore(BigDecimal userIncome, String earnCode, Integer minAmt, Integer maxAmt) {
        if (earnCode == null || earnCode.isBlank()) return 0;
        if (earnCode.equals("0043002")) { // 소득금액조건
            BigDecimal min = (minAmt != null) ? new BigDecimal(minAmt) : BigDecimal.ZERO;
            BigDecimal max = (maxAmt != null) ? new BigDecimal(maxAmt) : BigDecimal.valueOf(Integer.MAX_VALUE);
            return (userIncome.compareTo(min) >= 0 && userIncome.compareTo(max) <= 0) ? 5 : 0;
        }
        return 0; // 기타조건("0043001", "0043003")은 점수 없음
    }

    // ===== 3~7. 퍼스널 항목 점수 (각 6점) =====
    public static int calculateEnumScore(Enum<?> userValue, String policyCode, String noRestrictionCode, int scoreIfMatch) {
        if (userValue == null || policyCode == null || policyCode.isBlank()) return scoreIfMatch;

        String userCode = getCodeFromEnum(userValue);
        List<String> policyCodes = Arrays.asList(policyCode.split(","));
        return (policyCodes.contains(noRestrictionCode) || policyCodes.contains(userCode)) ? scoreIfMatch : 0;
    }

    public static String getCodeFromEnum(Enum<?> e) {
        switch (e.getDeclaringClass().getSimpleName()) {
            case "EducationLevel": return EducationLevel.valueOf(e.name()).getCode();
            case "EmploymentStatus": return EmploymentStatus.valueOf(e.name()).getCode();
            case "Major": return Major.valueOf(e.name()).getCode();
            case "Specialty": return Specialty.valueOf(e.name()).getCode();
            case "MaritalStatus": return MaritalStatus.valueOf(e.name()).getCode();
            default: return null;
        }
    }

    // ===== 8. 지역 점수 (10점) =====
    public static int calculateRegionScore(Region region, String policyZipCd, RegionCodeMapper regionCodeMapper) {
        if (region == null || policyZipCd == null || policyZipCd.isBlank()) return 10;
        List<String> userZipCodes = regionCodeMapper.findZipCodesBySido(region.getLabel());
        List<String> policyZipCodes = Arrays.asList(policyZipCd.split(","));
        return userZipCodes.stream().anyMatch(policyZipCodes::contains) ? 10 : 0;
    }

    // ===== 9. 키워드 유사도 점수 (30점) =====
    public static int calculateKeywordScore(GoalVO.GoalKeyword keyword, PolicyVO policy) {
        int score = 0;
        if (PolicyKeywordMapping.matchCategory(keyword, policy.getLclsfNm())) score += 10;
        if (PolicyKeywordMapping.matchSubcategory(keyword, policy.getMclsfNm())) score += 10;
        if (PolicyKeywordMapping.matchKeyword(keyword, policy.getPlcyKywdNm())) score += 10;
        return score;
    }

    public static boolean isAvailablePolicy(String aplyYmd, String bizPrdEndYmd) {
        LocalDate today = LocalDate.now();

        // aplyYmd 기준 체크
        if (aplyYmd != null && !aplyYmd.isBlank()) {
            // 예: "20250724 ~ 20250806"
            if (aplyYmd.contains("~")) {
                String[] parts = aplyYmd.split("~");
                if (parts.length == 2) {
                    String endDateStr = parts[1].trim();
                    try {
                        LocalDate endDate = LocalDate.parse(endDateStr);
                        return endDate.isAfter(today); // 종료일이 오늘 이후면 신청 가능
                    } catch (Exception e) {
                        // 날짜 파싱 실패 → 일단 넘어가서 bizPrdEndYmd로 체크
                    }
                }
            }
        }

        // 2️aplyYmd가 없거나 이상하면 bizPrdEndYmd 기준 체크
        if (bizPrdEndYmd != null && !bizPrdEndYmd.isBlank()) {
            try {
                LocalDate endDate = LocalDate.parse(bizPrdEndYmd);
                return endDate.isAfter(today); // 종료일이 오늘 이후면 신청 가능
            } catch (Exception e) {
                return true; // bizPrdEndYmd도 이상하면 포함시킴
            }
        }

        // 둘 다 없거나 이상 → 포함시킴
        return true;
    }

}

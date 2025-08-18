package org.iebbuda.mozi.domain.recommend.util;

import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.mapper.RegionCodeMapper;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class PolicyScoreCalculator {

    private static final DateTimeFormatter BIZ_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter APPLY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /* ================== 종합 점수 (99점 만점) ================== */
    public static int calculateTotalScore(
            UserProfileVO profile,
            GoalVO.GoalKeyword keyword,
            PolicyVO policy,
            RegionCodeMapper regionCodeMapper
    ) {
        int total = 0;

        // 1) 기간 점수 (최대 10)
        total += calculatePeriodScore(policy.getAplyYmd(), policy.getBizPrdBgngYmd(), policy.getBizPrdEndYmd());

        // 2) 연령 (0/20)
        total += calculateAgeScoreStrict(profile.getAge(), policy.getSprtTrgtMinAge(), policy.getSprtTrgtMaxAge());

        // 3) 연소득 (0/5)
        total += calculateIncomeScore(profile.getAnnualIncome(), policy.getEarnCndSeCd(), policy.getEarnMinAmt(), policy.getEarnMaxAmt());

        // 4) 퍼스널 5종 각각 5점(총 25)
        total += calculateEnumScore5(profile.getMaritalStatus(),    policy.getMrgSttsCd(),    MaritalStatus.NO_RESTRICTION.getCode());
        total += calculateEnumScore5(profile.getEducationLevel(),   policy.getSchoolCd(),     EducationLevel.NO_RESTRICTION.getCode());
        total += calculateEnumScore5(profile.getEmploymentStatus(), policy.getJobCd(),        EmploymentStatus.NO_RESTRICTION.getCode());
        total += calculateEnumScore5(profile.getMajor(),            policy.getPlcyMajorCd(),  Major.NO_RESTRICTION.getCode());
        total += calculateEnumScore5(profile.getSpecialty(),        policy.getSbizCd(),       Specialty.NO_RESTRICTION.getCode());

        // 5) 지역 (최대 9)
        total += calculateRegionScore99(profile.getRegion(), policy.getZipCd(), regionCodeMapper);

        // 6) 카테고리/키워드 (대 6 / 중 12 / 키 12, 비선호 -6)
        total += calculateKeywordScore99(keyword, policy);

        // 안전상한
        return Math.min(total, 99);
    }

    /* ===== 1. 기간 점수 (최대 10) =====
       - 운영기간 포함: +4
       - 신청기간:
         * "상시": +5
         * 기간 내: 기본 +4, 마감 임박 보너스 (D-7 이내 +1, D-3 이내 +2 → 최대 +6)
         * 그 외: +0
    */
    public static int calculatePeriodScore(String aplyYmd, String bizBeg, String bizEnd) {
        int score = 0;
        LocalDate today = LocalDate.now();

        // 운영기간
        if (isWithinBizPeriod(today, bizBeg, bizEnd)) score += 4;

        // 신청기간
        if (aplyYmd == null || aplyYmd.isBlank() || aplyYmd.contains("상시")) {
            score += 5; // 상시
            return score;
        }

        LocalDate[] range = parseApplyRange(aplyYmd);
        if (range == null) return score;

        LocalDate start = range[0];
        LocalDate end   = range[1];

        if (!today.isBefore(start) && !today.isAfter(end)) {
            int base = 4;
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, end);
            int bonus = 0;
            if (daysLeft <= 3) bonus = 2;
            else if (daysLeft <= 7) bonus = 1;
            score += Math.min(6, base + bonus);
        }
        return score;
    }

    /* ===== 2. 연령 점수 (0 or 20) ===== */
    public static int calculateAgeScoreStrict(int userAge, Integer minAge, Integer maxAge) {
        if (minAge == null && maxAge == null) return 20;
        int min = (minAge != null) ? minAge : Integer.MIN_VALUE;
        int max = (maxAge != null) ? maxAge : Integer.MAX_VALUE;
        return (userAge >= min && userAge <= max) ? 20 : 0;
    }

    /* ===== 3. 연소득 점수 (0 or 5) ===== */
    public static int calculateIncomeScore(BigDecimal userIncome, String earnCode, Integer minAmt, Integer maxAmt) {
        if (userIncome == null) return 0;

        if ("0043001".equals(earnCode)) return 5; // 제한없음
        if ("0043003".equals(earnCode)) return 5; // 기타(텍스트 조건)

        if ("0043002".equals(earnCode)) {         // 범위형
            BigDecimal min = (minAmt != null) ? BigDecimal.valueOf(minAmt) : BigDecimal.ZERO;
            BigDecimal max = (maxAmt != null) ? BigDecimal.valueOf(maxAmt) : BigDecimal.valueOf(Integer.MAX_VALUE);
            return (userIncome.compareTo(min) >= 0 && userIncome.compareTo(max) <= 0) ? 5 : 0;
        }

        return 0;
    }

    /* ===== 4. 퍼스널 항목 (항목당 5점) ===== */
    public static int calculateEnumScore5(Enum<?> userValue, String policyCode, String noRestrictionCode) {
        // 정책이 제한없음이거나, 사용자가 null이면 가점(비차별)
        if (policyCode == null || policyCode.isBlank()) return 5;
        if (userValue == null) return 5;

        String userCode = getCodeFromEnum(userValue);
        List<String> policyCodes = Arrays.asList(policyCode.split(","));

        return (policyCodes.contains(noRestrictionCode) || policyCodes.contains(userCode)) ? 5 : 0;
    }

    public static String getCodeFromEnum(Enum<?> e) {
        switch (e.getDeclaringClass().getSimpleName()) {
            case "EducationLevel":  return EducationLevel.valueOf(e.name()).getCode();
            case "EmploymentStatus":return EmploymentStatus.valueOf(e.name()).getCode();
            case "Major":           return Major.valueOf(e.name()).getCode();
            case "Specialty":       return Specialty.valueOf(e.name()).getCode();
            case "MaritalStatus":   return MaritalStatus.valueOf(e.name()).getCode();
            default:                return null;
        }
    }

    /* ===== 5. 지역 점수 (최대 9) =====
       - 사용자 ZIP과 정책 ZIP 교집합 있으면 9
       - 정책 ZIP 없음(or 전국 추정) 3
       - 그 외 0
    */
    public static int calculateRegionScore99(Region region, String policyZipCd, RegionCodeMapper regionCodeMapper) {
        if (region == null) return 3; // 사용자 지역 모르면 약한 중립점

        if (policyZipCd == null || policyZipCd.isBlank()) {
            return 3; // 정보없음 → 전국 유사 취급
        }

        List<String> policyZipCodes = Arrays.asList(policyZipCd.split(","));
        // 전국 추정(휴리스틱): ZIP이 아주 많으면 전국으로 간주
        if (policyZipCodes.size() >= 200) return 3;

        List<String> userZipCodes = regionCodeMapper.findZipCodesBySido(region.getLabel());
        if (userZipCodes == null || userZipCodes.isEmpty()) return 0;

        boolean anyMatch = userZipCodes.stream().anyMatch(policyZipCodes::contains);
        return anyMatch ? 9 : 0;
    }

    /* ===== 6. 카테고리/키워드 점수 (최대 30) =====
       - 대분류 6 / 중분류 12 / 키워드 12
       - 비선호 중분류면 -6 (하한 0 유지)
    */
    // 6. 카테고리/키워드 점수 (최대 30)
    public static int calculateKeywordScore99(GoalVO.GoalKeyword keyword, PolicyVO policy) {
        int score = 0;

        if (PolicyKeywordMapping.matchCategory(keyword,  policy.getLclsfNm()))     score += 6;
        if (PolicyKeywordMapping.matchSubcategory(keyword, policy.getMclsfNm()))   score += 12;
        if (PolicyKeywordMapping.matchKeyword(keyword,    policy.getPlcyKywdNm())) score += 12;

        // 비선호 중분류 감점
        if (PolicyKeywordMapping.matchNegativeSubcategory(keyword, policy.getMclsfNm())) {
            score = Math.max(0, score - 6);
        }

        //  정책 키워드에 "보조금" 포함되면 +2
        if (containsKeyword(policy.getPlcyKywdNm(), "보조금")) {
            score += 2;
        }

        // 최대 30점 캡
        return Math.min(score, 30);
    }


    /* ===== 신청/운영 기간 유효성 (필터용) ===== */
    public static boolean isAvailablePolicy(String aplyYmd, String bizPrdBeg, String bizPrdEnd) {
        LocalDate today = LocalDate.now();

        if (!isWithinBizPeriod(today, bizPrdBeg, bizPrdEnd)) return false;

        if (aplyYmd == null || aplyYmd.isBlank()) return true; // 상시 간주
        if (aplyYmd.contains("상시")) return true;

        LocalDate[] range = parseApplyRange(aplyYmd);
        if (range == null) return false;

        return (!today.isBefore(range[0]) && !today.isAfter(range[1]));
    }

    public static LocalDate getApplyEndForSort(String aplyYmd) {
        if (aplyYmd == null || aplyYmd.isBlank()) return LocalDate.MAX;
        if (aplyYmd.contains("상시")) return LocalDate.MAX;

        LocalDate[] range = parseApplyRange(aplyYmd);
        if (range == null || range[1] == null) return LocalDate.MAX;
        return range[1];
    }

    /* ===== 내부 공통 ===== */
    private static boolean isWithinBizPeriod(LocalDate today, String begStr, String endStr) {
        LocalDate beg = parseBizDate(begStr);
        LocalDate end = parseBizDate(endStr);

        if (beg == null && end == null) return true;
        if (beg != null && end != null) return (!today.isBefore(beg) && !today.isAfter(end));
        if (beg != null) return !today.isBefore(beg);
        return !today.isAfter(end);
    }

    private static LocalDate parseBizDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim(), BIZ_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDate[] parseApplyRange(String aplyYmd) {
        if (aplyYmd == null || aplyYmd.isBlank()) return null;

        String s = aplyYmd.replace(" ", "");
        if (s.contains("상시")) return new LocalDate[]{ LocalDate.MIN, LocalDate.MAX };

        if (s.contains("~")) {
            String[] parts = s.split("~");
            if (parts.length >= 2) {
                LocalDate beg = tryParseApply(parts[0]);
                LocalDate end = tryParseApply(parts[1]);
                if (beg != null && end != null) return new LocalDate[]{ beg, end };
                return null;
            }
        }

        LocalDate d = tryParseApply(s);
        return (d != null) ? new LocalDate[]{ d, d } : null;
    }

    private static LocalDate tryParseApply(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), APPLY_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    // 정책 키워드 문자열에 특정 키워드 포함 여부 확인 함수
    private static boolean containsKeyword(String raw, String target) {
        if (raw == null || raw.isBlank() || target == null || target.isBlank()) return false;
        String targetNorm = target.trim().toLowerCase();
        return Arrays.stream(raw.split(","))
                .map(s -> s.trim().toLowerCase())
                .anyMatch(s -> s.equals(targetNorm) || s.contains(targetNorm));
    }

}

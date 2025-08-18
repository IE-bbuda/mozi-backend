package org.iebbuda.mozi.domain.recommend.util;

import org.iebbuda.mozi.domain.goal.domain.GoalVO.GoalKeyword;

import java.util.List;
import java.util.Map;

public class PolicyKeywordMapping {

    // 대분류 매핑
    public static final Map<GoalKeyword, List<String>> CATEGORY_MAP = Map.of(
            GoalKeyword.MARRIAGE,      List.of("복지문화", "주거"),
            GoalKeyword.EMPLOYMENT,    List.of("일자리", "교육"),
            GoalKeyword.HOME_PURCHASE, List.of("주거"),
            GoalKeyword.TRAVEL,        List.of("참여권리", "복지문화"),
            GoalKeyword.EDUCATION_FUND,List.of("교육"),
            GoalKeyword.HOBBY,         List.of("복지문화")
    );

    // 중분류 매핑
    public static final Map<GoalKeyword, List<String>> SUBCATEGORY_MAP = Map.of(
            GoalKeyword.MARRIAGE,      List.of("주택 및 거주지", "전월세 및 주거급여 지원"),
            GoalKeyword.EMPLOYMENT,    List.of("취업", "창업", "재직자", "장기미취업청년", "미래역량강화"),
            GoalKeyword.HOME_PURCHASE, List.of("주택 및 거주지", "전월세 및 주거급여 지원"),
            GoalKeyword.TRAVEL,        List.of("문화활동", "청년국제교류"),
            GoalKeyword.EDUCATION_FUND,List.of("교육비지원", "미래역량강화", "온라인교육"),
            GoalKeyword.HOBBY,         List.of("문화활동", "예술인지원", "건강", "정책인프라구축", "청년참여", "권익보호")
    );

    // 키워드 매핑
    public static final Map<GoalKeyword, List<String>> KEYWORD_MAP = Map.of(
            GoalKeyword.MARRIAGE,      List.of( "공공임대주택", "전월세", "청년가장", "출산", "육아"),
            GoalKeyword.EMPLOYMENT,    List.of("인턴", "맞춤형상담서비스", "장기미취업청년", "벤처", "중소기업"),
            GoalKeyword.HOME_PURCHASE, List.of("주거지원", "공공임대주택", "전월세", "금리혜택", "청년가장", "보조금"),
            GoalKeyword.TRAVEL,        List.of("해외진출", "바우처"),
            GoalKeyword.EDUCATION_FUND,List.of("교육지원", "보조금", "대출"),
            GoalKeyword.HOBBY,         List.of("바우처", "문화", "예술")
    );

    // 비선호 중분류(약 페널티용)
    public static final Map<GoalKeyword, List<String>> NEGATIVE_SUBCATEGORY_MAP = Map.of(
            GoalKeyword.MARRIAGE,      List.of("취업", "미래역량강화")
    );

    // 허용 중분류(프리필터) — 이 목록 안에 없으면 아예 점수 계산 대상에서 제외
    public static final Map<GoalKeyword, List<String>> ALLOWED_SUBCATEGORY_MAP = Map.of(
            GoalKeyword.MARRIAGE,      List.of("주택 및 거주지", "전월세 및 주거급여 지원"),
            GoalKeyword.HOME_PURCHASE, List.of("주택 및 거주지", "전월세 및 주거급여 지원"),
            GoalKeyword.EMPLOYMENT,    List.of("취업", "재직자", "장기미취업청년", "미래역량강화"),
            GoalKeyword.TRAVEL,        List.of("청년국제교류", "문화활동"),
            GoalKeyword.EDUCATION_FUND,List.of("교육비지원", "미래역량강화"),
            GoalKeyword.HOBBY,         List.of("문화활동", "예술인지원")
    );

    private PolicyKeywordMapping() {}

    public static boolean matchCategory(GoalKeyword keyword, String lclsfNm) {
        if (keyword == null || lclsfNm == null) return false;
        return CATEGORY_MAP.getOrDefault(keyword, List.of()).stream().anyMatch(lclsfNm::contains);
    }

    public static boolean matchSubcategory(GoalKeyword keyword, String mclsfNm) {
        if (keyword == null || mclsfNm == null) return false;
        return SUBCATEGORY_MAP.getOrDefault(keyword, List.of()).stream().anyMatch(mclsfNm::contains);
    }

    public static boolean matchKeyword(GoalKeyword keyword, String plcyKywdNm) {
        if (keyword == null || plcyKywdNm == null) return false;
        return KEYWORD_MAP.getOrDefault(keyword, List.of()).stream().anyMatch(plcyKywdNm::contains);
    }

    public static boolean matchNegativeSubcategory(GoalKeyword keyword, String mclsfNm) {
        if (keyword == null || mclsfNm == null) return false;
        return NEGATIVE_SUBCATEGORY_MAP.getOrDefault(keyword, List.of()).stream().anyMatch(mclsfNm::contains);
    }

}

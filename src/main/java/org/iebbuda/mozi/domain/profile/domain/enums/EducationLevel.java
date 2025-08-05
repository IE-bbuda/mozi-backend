package org.iebbuda.mozi.domain.profile.domain.enums;

public enum EducationLevel {
    NO_RESTRICTION("제한없음", "0049010"),
    LESS_THAN_HIGH("고등학교 미만", "0049001"),
    HIGH_SCHOOL_ENROLLED("고등학교 재학", "0049002"),
    HIGH_SCHOOL_EXPECTED("고등학교 졸업 예정", "0049003"),
    HIGH_SCHOOL("고등학교 졸업", "0049004"),
    COLLEGE_ENROLLED("전문대학 재학", "0049005"),
    COLLEGE_EXPECTED("전문대학 졸업 예정", "0049006"),
    COLLEGE("전문대학 졸업", "0049007"),
    GRADUATE("대학원", "0049008"),
    OTHER("기타", "0049009");

    private final String label;
    private final String code;

    EducationLevel(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static EducationLevel fromCode(String code) {
        for (EducationLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown education level code: " + code);
    }
}

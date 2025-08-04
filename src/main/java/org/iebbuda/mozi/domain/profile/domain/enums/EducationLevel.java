package org.iebbuda.mozi.domain.profile.domain.enums;

public enum EducationLevel {
    NO_RESTRICTION("제한없음"),
    LESS_THAN_HIGH("고등학교 미만"),
    HIGH_SCHOOL_ENROLLED("고등학교 재학"),
    HIGH_SCHOOL_EXPECTED("고등학교 졸업 예정"),
    HIGH_SCHOOL("고등학교 졸업"),
    COLLEGE_ENROLLED("전문대학 재학"),
    COLLEGE_EXPECTED("전문대학 졸업 예정"),
    COLLEGE("전문대학 졸업"),
    GRADUATE("대학원"),
    OTHER("기타");

    private final String label;

    EducationLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return this.name();
    }

    public static EducationLevel fromCode(String code) {
        for (EducationLevel level : EducationLevel.values()) {
            if (level.name().equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown education level code: " + code);
    }
}

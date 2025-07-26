package org.iebbuda.mozi.profile.domain.enums;

public enum EducationLevel {
    NO_RESTRICTION("제한없음"),
    HIGH_SCHOOL("고등학교"),
    COLLEGE("전문대학"),
    UNIVERSITY("대학교"),
    GRADUATE("대학원");

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

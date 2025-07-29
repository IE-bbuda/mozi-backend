package org.iebbuda.mozi.domain.profile.domain.enums;

public enum Major {
    NO_RESTRICTION("제한없음"),
    HUMANITIES("인문학"),
    SOCIAL_SCIENCE("사회과학"),
    BUSINESS("경영/경제"),
    LANGUAGE("언어/문학"),
    ENGINEERING("공학"),
    ARTS_SPORTS("예술/체육"),
    AGRICULTURE("농업/수산업"),
    OTHER("기타");

    private final String label;

    Major(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return this.name();
    }

    public static Major fromCode(String code) {
        for (Major major : Major.values()) {
            if (major.name().equals(code)) {
                return major;
            }
        }
        throw new IllegalArgumentException("Unknown major code: " + code);
    }
}

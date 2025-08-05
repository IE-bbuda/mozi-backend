package org.iebbuda.mozi.domain.profile.domain.enums;

public enum Major {
    NO_RESTRICTION("제한없음", "0011009"),
    HUMANITIES("인문학", "0011001"),
    SOCIAL_SCIENCE("사회과학", "0011002"),
    BUSINESS("경영/경제", "0011003"),
    LANGUAGE("언어/문학", "0011004"),
    ENGINEERING("공학", "0011005"),
    ARTS_SPORTS("예술/체육", "0011006"),
    AGRICULTURE("농업/수산업", "0011007"),
    OTHER("기타", "0011008");

    private final String label;
    private final String code;

    Major(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static Major fromCode(String code) {
        for (Major major : values()) {
            if (major.code.equals(code)) {
                return major;
            }
        }
        throw new IllegalArgumentException("Unknown major code: " + code);
    }
}

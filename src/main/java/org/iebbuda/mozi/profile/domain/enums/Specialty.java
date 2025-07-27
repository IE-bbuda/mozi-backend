package org.iebbuda.mozi.profile.domain.enums;

public enum Specialty {
    NO_RESTRICTION("제한없음"),
    WOMEN("여성"),
    SME("중소기업"),
    BASIC_LIVELIHOOD("기초생활수급자"),
    SINGLE_PARENT("한부모가정"),
    DISABLED("장애인"),
    FARMER("농업인"),
    MILITARY("군인/보훈대상자"),
    LOCAL_TALENT("지역인재"),
    OTHER("기타");

    private final String label;

    Specialty(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return this.name();
    }

    public static Specialty fromCode(String code) {
        for (Specialty specialty : Specialty.values()) {
            if (specialty.name().equals(code)) {
                return specialty;
            }
        }
        throw new IllegalArgumentException("Unknown specialty code: " + code);
    }
}

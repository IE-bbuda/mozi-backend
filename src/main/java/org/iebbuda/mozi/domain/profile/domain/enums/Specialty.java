package org.iebbuda.mozi.domain.profile.domain.enums;

public enum Specialty {
    NO_RESTRICTION("제한없음", "0014010"),
    SME("중소기업", "0014001"),
    WOMEN("여성", "0014002"),
    BASIC_LIVELIHOOD("기초생활수급자", "0014003"),
    SINGLE_PARENT("한부모가정", "0014004"),
    DISABLED("장애인", "0014005"),
    FARMER("농업인", "0014006"),
    MILITARY("군인/보훈대상자", "0014007"),
    LOCAL_TALENT("지역인재", "0014008"),
    OTHER("기타", "0014009");

    private final String label;
    private final String code;

    Specialty(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static Specialty fromCode(String code) {
        for (Specialty specialty : values()) {
            if (specialty.code.equals(code)) {
                return specialty;
            }
        }
        throw new IllegalArgumentException("Unknown specialty code: " + code);
    }
}

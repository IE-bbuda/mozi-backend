package org.iebbuda.mozi.domain.profile.domain.enums;

public enum EmploymentStatus {
    NO_RESTRICTION("제한없음", "0013010"),
    EMPLOYED("재직자", "0013001"),
    SELF_EMPLOYED("자영업자", "0013002"),
    UNEMPLOYED("미취업자", "0013003"),
    FREELANCER("프리랜서", "0013004"),
    DAILY_WORKER("일용근로자", "0013005"),
    STARTUP("(예비)창업자", "0013006"),
    SHORT_TERM("단기근로자", "0013007"),
    FARMER("영농종사자", "0013008"),
    OTHER("기타", "0013009");

    private final String label;
    private final String code;

    EmploymentStatus(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static EmploymentStatus fromCode(String code) {
        for (EmploymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown employment status code: " + code);
    }
}

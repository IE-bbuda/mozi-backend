package org.iebbuda.mozi.domain.profile.domain.enums;

public enum EmploymentStatus {
    NO_RESTRICTION("제한없음"),
    EMPLOYED("재직자"),
    SELF_EMPLOYED("자영업자"),
    UNEMPLOYED("미취업자"),
    FREELANCER("프리랜서"),
    DAILY_WORKER("일용근로자"),
    STARTUP("(예비)창업자"),
    SHORT_TERM("단기근로자"),
    FARMER("영농종사자"),
    OTHER("기타");

    private final String label;

    EmploymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return this.name();
    }

    public static EmploymentStatus fromCode(String code) {
        for (EmploymentStatus status : EmploymentStatus.values()) {
            if (status.name().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown employment status code: " + code);
    }
}

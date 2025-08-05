package org.iebbuda.mozi.domain.profile.domain.enums;

public enum MaritalStatus {
    NO_RESTRICTION("제한없음", "0055003"),
    SINGLE("미혼", "0055002"),
    MARRIED("기혼", "0055001");

    private final String label;
    private final String code;

    MaritalStatus(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }

    public static MaritalStatus fromCode(String code) {
        for (MaritalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown marital status code: " + code);
    }
}

package org.iebbuda.mozi.domain.profile.domain.enums;

public enum MaritalStatus {
    NO_RESTRICTION("제한없음"),
    SINGLE("미혼"),
    MARRIED("기혼");

    private final String label;

    MaritalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return this.name();
    }

    public static MaritalStatus fromCode(String code) {
        for (MaritalStatus status : MaritalStatus.values()) {
            if (status.name().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown marital status code: " + code);
    }
}

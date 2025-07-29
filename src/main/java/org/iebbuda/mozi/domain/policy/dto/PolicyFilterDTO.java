package org.iebbuda.mozi.domain.policy.dto;

import lombok.Data;

import java.util.List;

@Data
public class PolicyFilterDTO {
    private String marital_status; // 혼인여부 단일 선택
    private List<String> region;
    private List<String> job;
    private List<String> education;
    private List<String> major;
    private List<String> specialty;
    private Integer age;
}

package org.iebbuda.mozi.domain.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankLoginVO {
    private Integer bankLoginId;
    private Integer userId;
    private String bankCode;
    private String connectedId;
}

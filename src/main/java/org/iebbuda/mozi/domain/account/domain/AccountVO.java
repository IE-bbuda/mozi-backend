package org.iebbuda.mozi.domain.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVO {
    private Integer accountId;
    private Integer bankLoginId;
    private String accountNumber;
    private String accountName;
    private Double balance;
    private String currency;
    private Integer productType;
    private Boolean isMinus;
}

package org.iebbuda.mozi.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankSummaryDTO {
    private String bankCode;
    private Integer accountCount;
    @Builder.Default
    private Double totalBalance = 0.0;

    @Builder.Default
    private String representativeAccountName = "";
}

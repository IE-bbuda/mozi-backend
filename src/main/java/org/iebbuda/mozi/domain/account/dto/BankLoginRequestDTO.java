package org.iebbuda.mozi.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankLoginRequestDTO {
    private String bankCode;
    private String userBankId;
    private String userBankPassword;

}

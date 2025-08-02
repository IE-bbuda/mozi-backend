package org.iebbuda.mozi.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.DepositResponse;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositScrapDto {
    private Long scrapId;
    private Long userId;
    private LocalDateTime createdAt;
    private DepositResponse deposit; // 예금 상품 DTO 포함

}

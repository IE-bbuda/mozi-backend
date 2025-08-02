package org.iebbuda.mozi.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class SavingScrapDto {
    private Long scrapId;
    private Long userId;
    private LocalDateTime createdAt;
    private SavingResponse saving;
}
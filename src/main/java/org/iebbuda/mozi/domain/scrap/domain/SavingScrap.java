package org.iebbuda.mozi.domain.scrap.domain;

import lombok.Data;
import org.iebbuda.mozi.domain.product.domain.SavingProduct;

import java.time.LocalDateTime;

@Data
public class SavingScrap {
    private Long scrapId;
    private Long userId;
    private Long savingId;
    private LocalDateTime createdAt;
    private SavingProduct savingProduct;
}

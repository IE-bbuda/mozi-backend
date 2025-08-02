package org.iebbuda.mozi.domain.scrap.domain;

import lombok.Data;
import org.iebbuda.mozi.domain.product.domain.DepositProduct;

import java.time.LocalDateTime;

@Data
public class DepositScrap {
    private Long scrapId;
    private Long userId;
    private Long depositId;
    private LocalDateTime createdAt;//스크랩 생성일시
    private DepositProduct depositProduct;
}

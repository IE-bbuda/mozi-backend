package org.iebbuda.mozi.domain.scrap.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.scrap.converter.DepositScrapConverter;
import org.iebbuda.mozi.domain.scrap.converter.SavingScrapConverter;
import org.iebbuda.mozi.domain.scrap.dto.DepositScrapDto;
import org.iebbuda.mozi.domain.scrap.dto.SavingScrapDto;
import org.iebbuda.mozi.domain.scrap.mapper.DepositScrapMapper;
import org.iebbuda.mozi.domain.scrap.mapper.SavingScrapMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingScrapService {

    private final SavingScrapMapper savingScrapMapper;
    private final SavingScrapConverter savingScrapConverter;

    public List<SavingScrapDto> getUserScraps(Long userId) {
        return savingScrapMapper.getUserScraps(userId).stream()
                .map(savingScrapConverter::toResponse)
                .toList();
    }

    @Transactional
    public void addScrap(Long userId, Long depositId) {
        savingScrapMapper.addScrap(userId, depositId);
    }

    @Transactional
    public void removeScrap(Long userId, Long depositId) {
        savingScrapMapper.removeScrap(userId, depositId);
    }
}

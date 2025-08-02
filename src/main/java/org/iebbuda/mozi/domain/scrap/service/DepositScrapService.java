package org.iebbuda.mozi.domain.scrap.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.DepositResponse;
import org.iebbuda.mozi.domain.scrap.converter.DepositScrapConverter;
import org.iebbuda.mozi.domain.scrap.dto.DepositScrapDto;
import org.iebbuda.mozi.domain.scrap.mapper.DepositScrapMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositScrapService {

    private final DepositScrapMapper depositScrapMapper;
    private final DepositScrapConverter depositScrapConverter; // 추가

    public List<DepositScrapDto> getUserScraps(Long userId) {
        return depositScrapMapper.getUserScraps(userId).stream()
                .map(depositScrapConverter::toResponse)
                .toList();
    }

    @Transactional
    public void addScrap(Long userId, Long depositId) {
        depositScrapMapper.addScrap(userId, depositId);
    }

    @Transactional
    public void removeScrap(Long userId, Long depositId) {
        depositScrapMapper.removeScrap(userId, depositId);
    }
}
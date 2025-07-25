package org.iebbuda.mozi.product.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.product.domain.DepositProduct;
import org.iebbuda.mozi.product.dto.DepositResponse;
import org.iebbuda.mozi.product.service.DepositQueryService;
import org.iebbuda.mozi.product.service.DepositSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final DepositQueryService depositQueryService;

    //전체 정기예금 목록 조회
    @GetMapping
    public List<DepositResponse>getAllDepositProduct(){
        return depositQueryService.getAllDeposits();
    }

    //특정 정기예금 상세조회
    @GetMapping("/{id}")
    public DepositResponse getDepositProductById(@PathVariable Long id){
        return depositQueryService.getDepositById(id);
    }
}

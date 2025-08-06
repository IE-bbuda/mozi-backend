package org.iebbuda.mozi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.product.service.SavingQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class SavingController {

    private final SavingQueryService savingQueryService;

    //전체 적금 목록 조회
    @GetMapping
    public List<SavingResponse> getAllSavingProduct(){
        return savingQueryService.getAllSavings();
    }

    //특정 적금 상세조회
    @GetMapping("/{id}")
    public SavingResponse getSavingProductById(@PathVariable Long id){
        return savingQueryService.getSavingById(id);
    }

    @GetMapping("/top")
    public List<SavingResponse>getTopSavingProduct(){
        return savingQueryService.getTopSavingProduct(2);
    }
}

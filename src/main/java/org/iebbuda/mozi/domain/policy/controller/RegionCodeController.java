package org.iebbuda.mozi.domain.policy.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;
import org.iebbuda.mozi.domain.policy.service.RegionCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionCodeController {

    private final RegionCodeService regionCodeService;


    // 전체 지역 반환
    @GetMapping
    public List<RegionCodeVO> getAllRegionCodes() {
        return regionCodeService.getAllRegionCodes();
    }


    // zip 코드 리스트로 변환
    @PostMapping("/zipcodes")
    public List<String> getZipCodesByRegionNames(@RequestBody List<String> regionNames) {
        return regionCodeService.getZipCodesByRegionNames(regionNames);
    }

    // 지역명 리스트로 변환
    @PostMapping("/names")
    public List<String> getRegionNamesByZipCodes(@RequestBody List<String> zipCodes) {
        return regionCodeService.getRegionNamesByZipCodes(zipCodes);
    }

    // 시도(sido) 기준 zip_code 전체 반환
    @GetMapping("/zipcodes/sido")
    public ResponseEntity<List<String>> getZipCodesBySido(@RequestParam String sido) {
        return ResponseEntity.ok(regionCodeService.findZipCodesBySido(sido));
    }


//    // zipCd DB에 저장
//    @PostMapping("/fetch")
//    public ResponseEntity<String> fetchAndSave() {
//        regionCodeService.fetchAndSaveFromApi();
//        return ResponseEntity.ok(" RegionCode DB 저장 완료");
//    }
}

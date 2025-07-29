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

    @PostMapping("/zipcodes")
    public List<String> getZipCodesByRegionNames(@RequestBody List<String> regionNames) {
        return regionCodeService.getZipCodesByRegionNames(regionNames);
    }

    @PostMapping("/add")
    public void insertRegionCode(@RequestBody RegionCodeVO regionCode) {
        regionCodeService.saveRegionCode(regionCode);
    }

    @GetMapping("/all")
    public List<RegionCodeVO> getAllRegionCodes() {
        return regionCodeService.getAllRegionCodes();
    }

    @PostMapping("/names")
    public List<String> getRegionNamesByZipCodes(@RequestBody List<String> zipCodes) {
        return regionCodeService.getRegionNamesByZipCodes(zipCodes);
    }
    // zipCd DB에 저장
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAndSave() {
        regionCodeService.fetchAndSaveFromApi();
        return ResponseEntity.ok(" RegionCode DB 저장 완료");
    }
}

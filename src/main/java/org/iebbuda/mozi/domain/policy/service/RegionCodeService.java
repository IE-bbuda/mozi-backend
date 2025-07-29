package org.iebbuda.mozi.domain.policy.service;

import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;

import java.util.List;

public interface RegionCodeService {
    List<String> getZipCodesByRegionNames(List<String> regionNames);
    List<String> getRegionNamesByZipCodes(List<String> zipCodes);
    void saveRegionCode(RegionCodeVO regionCode);
    List<RegionCodeVO> getAllRegionCodes();

    // zipCd API 호출 후 저장
    void fetchAndSaveFromApi();

}

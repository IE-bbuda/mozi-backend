package org.iebbuda.mozi.domain.policy.service;

import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;

import java.util.List;

public interface RegionCodeService {

    // 지역명으로 zip 코드 조회
    List<String> getZipCodesByRegionNames(List<String> regionNames);

    // zip 코드로 지역명 조회
    List<String> getRegionNamesByZipCodes(List<String> zipCodes);

    // 전체 지역코드 목록 조회
    List<RegionCodeVO> getAllRegionCodes();

    // zipCd API 호출 후 저장
    void fetchAndSaveFromApi();



}

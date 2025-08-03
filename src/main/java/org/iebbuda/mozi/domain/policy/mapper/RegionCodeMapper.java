package org.iebbuda.mozi.domain.policy.mapper;

import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RegionCodeMapper {

    // DB에 insert
    void insertRegionCode(RegionCodeVO regionCode);

    // 지역명 리스트로 zip 코드 조회
    List<String> findZipCodesByRegionNames(List<String> regionNames); // 예: ["서울특별시 강북구", "경기도 성남시"]

    // zip 코드 리스트로 지역명 조회
    List<String> findRegionNamesByZipCodes(List<String> zipCodes);

    // 전체 지역코드 리스트 조회
    List<RegionCodeVO> findAll();

    // 시,도로 조회
    List<String> findZipCodesBySido(String sido);

}

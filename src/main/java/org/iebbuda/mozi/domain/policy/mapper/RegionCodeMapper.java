package org.iebbuda.mozi.domain.policy.mapper;

import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RegionCodeMapper {
    void insertRegionCode(RegionCodeVO regionCode);

    List<String> findZipCodesByRegionNames(List<String> regionNames); // 예: ["서울특별시 강북구", "경기도 성남시"]

    List<String> findRegionNamesByZipCodes(List<String> zipCodes);

    List<RegionCodeVO> findAll();

    void truncateTable();


}

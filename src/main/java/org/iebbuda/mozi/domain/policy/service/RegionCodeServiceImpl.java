package org.iebbuda.mozi.domain.policy.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.policy.domain.RegionCodeVO;
import org.iebbuda.mozi.domain.policy.mapper.RegionCodeMapper;
import org.iebbuda.mozi.domain.policy.util.RegionCodeApiCaller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegionCodeServiceImpl implements RegionCodeService {

    private final RegionCodeMapper regionCodeMapper;
    private final RegionCodeApiCaller regionCodeApiCaller;


    @Override
    public List<String> getZipCodesByRegionNames(List<String> regionNames) {
        return regionCodeMapper.findZipCodesByRegionNames(regionNames);
    }

    @Override
    public List<String> getRegionNamesByZipCodes(List<String> zipCodes) {
        return regionCodeMapper.findRegionNamesByZipCodes(zipCodes);
    }

    @Override
    public void saveRegionCode(RegionCodeVO regionCode) {
        regionCodeMapper.insertRegionCode(regionCode);
    }

    @Override
    public List<RegionCodeVO> getAllRegionCodes() {
        return regionCodeMapper.findAll();
    }

    // 외부 API 호출 후 DB 저장
    @Override
    public void fetchAndSaveFromApi() {


        Map<String, Map<String, String>> regionMap = regionCodeApiCaller.fetchAllZipCodes(100, 500);
        int count = 0;

        for (String sido : regionMap.keySet()) {
            Map<String, String> sigunguMap = regionMap.get(sido);
            for (Map.Entry<String, String> entry : sigunguMap.entrySet()) {
                RegionCodeVO region = new RegionCodeVO();
                region.setSido(sido);
                region.setSigungu(entry.getKey());
                region.setZipCode(entry.getValue());

                try {
                    regionCodeMapper.insertRegionCode(region);
                    count++;
                } catch (Exception e) {
                    System.out.printf("⚠️ 저장 실패: %s %s (%s)%n", sido, entry.getKey(), entry.getValue());
                }
            }
        }

        System.out.println("✅ 저장 완료. 총 " + count + "개 저장됨.");
    }

}

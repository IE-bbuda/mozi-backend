package org.iebbuda.mozi.domain.scrap.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.scrap.domain.PolicyScrapVO;
import org.iebbuda.mozi.domain.scrap.mapper.PolicyScrapMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final PolicyScrapMapper policyScrapMapper;

    @Override
    public void scrapPolicy(int userId, String plcyNo) {
        if (!policyScrapMapper.existsScrap(userId, plcyNo)) {
            PolicyScrapVO vo = new PolicyScrapVO();
            vo.setUserId(userId);
            vo.setPlcyNo(plcyNo);
            policyScrapMapper.insertScrap(vo);
            System.out.println("스크랩 시도: userId=" + userId + ", plcyNo=" + plcyNo);
        }
    }

    @Override
    public void cancelScrapPolicy(int userId, String plcyNo) {
        policyScrapMapper.deleteScrap(userId, plcyNo);
    }

    @Override
    public boolean isScrapedPolicy(int userId, String plcyNo) {
        return policyScrapMapper.existsScrap(userId, plcyNo);
    }

    @Override
    public List<String> getScrapedPolicyNos(int userId) {
        System.out.println("[DEBUG] getScrapedPolicyNos 호출됨 - userId = " + userId);
        return policyScrapMapper.getScrapPlcyNos(userId);
    }

    @Override
    public List<PolicyVO> getScrapedPolicies(int userId) {
        return policyScrapMapper.getScrapedPolicies(userId);
    }
}

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
    public void scrapPolicy(int userId, int policyId) {
        if (!policyScrapMapper.existsScrap(userId, policyId)) {
            PolicyScrapVO vo = new PolicyScrapVO();
            vo.setUserId(userId);
            vo.setPolicyId(policyId);
            policyScrapMapper.insertScrap(vo);
            System.out.println("스크랩 시도: userId=" + userId + ", policyId=" + policyId);

        }
    }

    @Override
    public void cancelScrapPolicy(int userId, int policyId) {
        policyScrapMapper.deleteScrap(userId, policyId);
    }

    @Override
    public boolean isScrapedPolicy(int userId, int policyId) {
        return policyScrapMapper.existsScrap(userId, policyId);
    }

    @Override
    public List<Integer> getScrapedPolicyIds(int userId) {
        System.out.println("[DEBUG] getScrapedPolicyIds 호출됨 - userId = " + userId);
        return policyScrapMapper.getScrapPolicyIds(userId);
    }

    @Override
    public List<PolicyVO> getScrapedPolicies(int userId) {
        return policyScrapMapper.getScrapedPolicies(userId);
    }
}

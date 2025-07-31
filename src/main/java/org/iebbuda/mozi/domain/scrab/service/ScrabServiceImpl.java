package org.iebbuda.mozi.domain.scrab.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.scrab.domain.PolicyScrabVO;
import org.iebbuda.mozi.domain.scrab.mapper.PolicyScrabMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrabServiceImpl implements ScrabService{

    private final PolicyScrabMapper policyScrabMapper;

    @Override
    public void scrabPolicy(int userId, int policyId) {
        if (!policyScrabMapper.existsScrab(userId, policyId)) {
            PolicyScrabVO vo = new PolicyScrabVO();
            vo.setUserId(userId);
            vo.setPolicyId(policyId);
            policyScrabMapper.insertScrab(vo);
        }
    }

    @Override
    public void cancelScrabPolicy(int userId, int policyId) {
        policyScrabMapper.deleteScrab(userId, policyId);
    }

    @Override
    public boolean isScrabbedPolicy(int userId, int policyId) {
        return policyScrabMapper.existsScrab(userId, policyId);
    }

    @Override
    public List<Integer> getScrabbedPolicyIds(int userId) {
        return policyScrabMapper.getScrabPolicyIds(userId);
    }
}

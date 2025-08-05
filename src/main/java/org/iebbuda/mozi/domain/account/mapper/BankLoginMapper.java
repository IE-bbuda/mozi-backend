package org.iebbuda.mozi.domain.account.mapper;

import org.iebbuda.mozi.domain.account.domain.BankLoginVO;

import java.util.List;

public interface BankLoginMapper {
    void add(BankLoginVO bankLogin);

    BankLoginVO getById(Integer bankLoginId);

    List<BankLoginVO> getByUserID(Integer userID);

    void deleteById(Integer bankLoginId);

    List<String> getBankCodeByUserId(Integer userId);

    List<String> getAllBanks();
}

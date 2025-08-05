package org.iebbuda.mozi.domain.account.service;

import org.iebbuda.mozi.domain.account.domain.AccountVO;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;

import java.util.List;
import java.util.Map;

public interface AccountService {
    List<AccountVO> fetchAccounts(String bankCode, String connectedId);
    public Map<String, Object> getAccounts(Integer userId);
    boolean addAccounts(BankLoginRequestDTO dto, Integer userId);
    List<AccountVO> refreshAccounts(Integer userId);
    List<String> deleteAccounts(List<String> bankCodeList, Integer userId);
    Map<String, Object> getBankSummary(Integer userId);
    Map<String, Object> getMainBankSummary(Integer userId);
    Map<String, Object> getAccountsByBank(String bankCode, Integer userId);
    public Map<String, Object> getBank(Integer userId);
    Map<String, Object> getAccountsByGoal(Integer goalId, Integer userId);
    Map<String, Object> updateMainBankCode(String bankCode, Integer userId);
    Map<String, Object> updateAccountsByGoal(List<String> accountNumberList, Integer goalId, Integer userId);
    Map<String, Object> getConnectedBanks(Integer userId);
}

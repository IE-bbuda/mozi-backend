package org.iebbuda.mozi.domain.account.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.account.domain.AccountVO;
import org.iebbuda.mozi.domain.account.dto.AccountResponseDTO;
import org.iebbuda.mozi.domain.account.dto.BankSummaryDTO;

import java.util.List;

public interface AccountMapper {
    List<AccountResponseDTO> getAccountByUserID(Integer userId);

    List<String> getAccountNumberByBankLoginId(Integer bankLoginId);

    void add(AccountVO account);

    void updateBalance(AccountVO account);

    void deleteByBankLoginId(Integer bankLoginId);

    List<BankSummaryDTO> getBankSummaryByUserId(Integer userId);

    List<AccountVO> getAccountsByUserIdAndBank(@Param("userId")Integer userId, @Param("bankCode") String bankCode);

    List<AccountResponseDTO> findAccountsByUserAndGoal(Integer userId, Integer goalId);

    String getMainBankCodeByUserId(Integer userId);

    void clearMainBankByUserId(Integer userId);

    int updateMainBankCodeByUserId(@Param("bankCode") String bankCode, @Param("userId") Integer userId);

    int clearGoalFromAccounts(@Param("goalId")Integer goalId, @Param("userId") Integer userId);

    int assignGoalToAccount(@Param("accountNumber")String accountNumber, @Param("goalId")Integer goalId, @Param("userId") Integer userId);

}

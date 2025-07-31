package org.iebbuda.mozi.domain.account.service;

import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;

public interface ConnectionService {

    //사용자 인증정보를 기반으로 외부 API에 계좌 연결 요청을 보내고 connectedId를 반환한다.
    String connect(BankLoginRequestDTO dto);
}

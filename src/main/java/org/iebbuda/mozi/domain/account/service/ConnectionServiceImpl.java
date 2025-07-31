package org.iebbuda.mozi.domain.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;
import org.iebbuda.mozi.domain.account.encrypt.RsaEncryptor;
import org.iebbuda.mozi.domain.account.external.ExternalApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final RsaEncryptor rsaEncryptor;
    private final AuthService authService;
    private final ExternalApiClient externalApiClient;


    @Override
    public String connect(BankLoginRequestDTO dto) {
        // 1. Codef 계정 등록 API URL
        String url = "https://development.codef.io/v1/account/create";

        // 2. 비밀번호 암호화
        String encryptedPassword = rsaEncryptor.encrypt(dto.getUserBankPassword());

        // 3. 생년월일 6자리 변환 (예: 19990101 → 990101)
        // String birthDate6 = dto.getBirthDate().substring(2);

        // 4. 요청 바디 구성
        Map<String, Object> requestBody = Map.of(
                "accountList", List.of(Map.of(
                        "countryCode", "KR",
                        "businessType", "BK",
                        "organization", dto.getBankCode(),
                        "clientType", "P",
                        "loginType", "1",
                        "id", dto.getUserBankId(),
                        "password", encryptedPassword
                        // ,"birthDate", birthDate6 // ← 추후 필요 시 포함
                ))
        );

        // 5. 헤더 구성
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer " + authService.getAccessToken()
        );

        // 6. 외부 API 호출
        ResponseEntity<String> response = externalApiClient.post(
                url, headers, requestBody, String.class
        );

        String encoded = response.getBody();
        System.out.println("Raw response: " + encoded);

        // 7. URL 디코딩
        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        System.out.println("Decoded response: " + decoded);

        // 8. JSON 파싱 및 connectedId 추출
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = mapper.readValue(decoded, Map.class);

            Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
            if (data == null || !data.containsKey("connectedId")) {
//                Map<String, Object> result = (Map<String, Object>) resultMap.get("result");
//                String message = result != null ? (String) result.get("message") : "은행 연결에 실패하였습니다.";
//                throw new BankLoginFailedException(message);
                return "";
            }

            return (String) data.get("connectedId");
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
package org.iebbuda.mozi.product.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.product.dto.DepositApiResponse;
import org.iebbuda.mozi.product.dto.SavingApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class FinancialApiService {

    @Value("${fss.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public FinancialApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DepositApiResponse callDepositApi(int pageNo) {
        String url = String.format(
                "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=%s&topFinGrpNo=020000&pageNo=%d",
                apiKey, pageNo
        );

        log.info("정기예금 API 호출: {}", url);
        ResponseEntity<DepositApiResponse> response =
                restTemplate.getForEntity(url, DepositApiResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("정기예금 API 호출 실패: " + response.getStatusCode());
        }
    }

    public SavingApiResponse callSavingApi(int pageNo) {
        String url = String.format(
                "https://finlife.fss.or.kr/finlifeapi/savingProductsSearch.json?auth=%s&topFinGrpNo=020000&pageNo=%d",
                apiKey, pageNo
        );

        log.info("적금 API 호출: {}", url);
        ResponseEntity<SavingApiResponse> response =
                restTemplate.getForEntity(url, SavingApiResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("적금 API 호출 실패: " + response.getStatusCode());
        }
    }
}

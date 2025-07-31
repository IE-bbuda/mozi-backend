package org.iebbuda.mozi.domain.account.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalApiClient {

    private final RestTemplate restTemplate;

    //post요청
    public <T> ResponseEntity<T> post(String url, Map<String, String> headers, Object requestBody, Class<T> responseType){
        HttpHeaders httpHeaders=buildHeaders(headers);

        HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, responseType);
    }

    //요청 헤더 만들기
    private HttpHeaders buildHeaders(Map<String, String> headers){
        HttpHeaders httpHeaders=new HttpHeaders();
        if(headers!=null){
            headers.forEach((k,v)->{httpHeaders.set(k,v);});
        }
        return httpHeaders;
    }
}

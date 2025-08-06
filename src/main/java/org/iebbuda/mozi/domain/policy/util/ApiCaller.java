package org.iebbuda.mozi.domain.policy.util;

import com.google.gson.*;
import org.iebbuda.mozi.domain.policy.dto.PolicyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApiCaller {

    // API 호출 URL과 인증키는 application.properties에서 주입받음
    @Value("${youth.api.url}")
    private String apiUrl;

    @Value("${youth.api.key}")
    private String apiKey;

    // 실제 요청 최종 URL
    public String getRequestUrl() {
        return apiUrl + "?apiKeyNm=" + apiKey + "&rtnType=json&pageNum=1&pageSize=1000";
    }

    public String getJsonResponse() {
        StringBuilder response = new StringBuilder();

        try {
            String fullUrl = getRequestUrl();
            System.out.println("🌐 최종 요청 URL: " + fullUrl);

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
            } else {
                System.out.println("❗ HTTP Error: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    // JSON 응답을 파싱하여 정책 DTO 리스트로 변환
    public List<PolicyDTO> parseJsonToPolicies(String json) {
        List<PolicyDTO> list = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = root.getAsJsonObject("result");

            if (result.has("youthPolicyList")) {
                JsonArray dataList = result.getAsJsonArray("youthPolicyList");
                Gson gson = new Gson();

                for (JsonElement element : dataList) {
                    PolicyDTO dto = gson.fromJson(element, PolicyDTO.class);
                    list.add(dto);
                }

                System.out.println("파싱된 정책 수: " + list.size());
            } else {
                System.out.println("youthPolicyList' 항목이 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}

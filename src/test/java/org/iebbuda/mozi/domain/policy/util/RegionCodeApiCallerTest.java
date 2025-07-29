package org.iebbuda.mozi.domain.policy.util;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RegionCodeApiCallerTest {

    public static void main(String[] args) {
        RegionCodeApiCaller caller = new RegionCodeApiCaller();
        caller.setApiKey("YOUR_API_KEY");
        caller.setApiUrl("YOUR_API_URL");

        // Map<시도, Map<구/군, 코드>>
        Map<String, Map<String, String>> result = caller.fetchAllZipCodes(100, 500);

        int totalCount = 0;

        for (String sido : result.keySet()) {
            System.out.println("[" + sido + "]");
            Map<String, String> guMap = result.get(sido);
            for (Map.Entry<String, String> entry : guMap.entrySet()) {
                System.out.println("  - " + entry.getKey() + " (" + entry.getValue() + ")");
                totalCount++;
            }
        }

        System.out.println("\n✅ 총 zip 코드 개수: " + totalCount + "개");
    }
// ✅ 총 zip 코드 개수: 264개
}
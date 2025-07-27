package org.iebbuda.mozi.domain.profile.domain.enums;

public enum Region {

        SEOUL("서울"),
        BUSAN("부산"),
        DAEGU("대구"),
        INCHEON("인천"),
        GWANGJU("광주"),
        DAEJEON("대전"),
        ULSAN("울산"),
        SEJONG("세종"),
        GYEONGGI("경기"),
        GANGWON("강원"),
        CHUNGBUK("충북"),
        CHUNGNAM("충남"),
        JEONBUK("전북"),
        JEONNAM("전남"),
        GYEONGBUK("경북"),
        GYEONGNAM("경남"),
        JEJU("제주");

        private final String label;

        Region(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getCode() {
            return this.name();
        }

        // 코드로 enum 찾기
        public static Region fromCode(String code) {
            for (Region region : Region.values()) {
                if (region.name().equals(code)) {
                    return region;
                }
            }
            throw new IllegalArgumentException("Unknown region code: " + code);
        }
}

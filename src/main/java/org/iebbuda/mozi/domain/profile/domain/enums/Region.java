package org.iebbuda.mozi.domain.profile.domain.enums;

public enum Region {

    SEOUL("서울특별시"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    GWANGJU("광주광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    JEONBUK("전북특별자치도"),
    JEONNAM("전라남도"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    JEJU("제주특별자치도");

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

    public static Region fromCode(String code) {
        for (Region region : values()) {
            if (region.name().equals(code)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region code: " + code);
    }

    public static Region fromLabel(String label) {
        for (Region region : values()) {
            if (region.label.equals(label)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region label: " + label);
    }
}

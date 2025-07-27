package org.iebbuda.mozi.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum BaseResponseStatus {
    /**
     * 성공 코드 2xx
     * 코드의 원활한 이해을 위해 code는 숫자가 아닌 아래 형태로 입력해주세요.
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    // 인증 관련
    FAIL_TOKEN_AUTHORIZATION(false, HttpStatus.UNAUTHORIZED.value(), "토큰 인증에 실패하였습니다."),
    INVALID_MEMBER(false, HttpStatus.NOT_FOUND.value(), "유효하지 않은 회원입니다."),
    USER_NOT_AUTHENTICATED(false, HttpStatus.UNAUTHORIZED.value(), "사용자가 인증되지 않았습니다."),

    // 프로필 관련
    PROFILE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "프로필을 찾을 수 없습니다."),
    PROFILE_SAVE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "프로필 저장에 실패했습니다."),
    INVALID_PROFILE_DATA(false, HttpStatus.BAD_REQUEST.value(), "잘못된 프로필 데이터입니다."),

    // 공통 에러
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 입력에 실패했습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."),
    FAIL_IMAGE_CONVERT(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Multipart 파일 전환에 실패했습니다.");
    private final boolean isSuccess;
    private final int code;
    private final String message;

    /**
     * isSuccess : 요청의 성공 또는 실패
     * code : Http Status Code
     * message : 설명
     */

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

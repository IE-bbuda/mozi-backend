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

    // 회원가입 관련
    DUPLICATE_LOGIN_ID(false, HttpStatus.CONFLICT.value(), "이미 존재하는 로그인 ID입니다."),
    DUPLICATE_EMAIL(false, HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다."),
    // 본인 확인 관련
    USER_VERIFICATION_SUCCESS(true, HttpStatus.OK.value(), "본인 확인이 완료되었습니다. 새 비밀번호를 설정해주세요."),
    USER_NOT_FOUND_FOR_RESET(false, HttpStatus.NOT_FOUND.value(), "입력하신 정보로 가입된 계정이 없습니다."),

    //비밀번호 재설정
    SESSION_CREATE_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "세션 생성에 실패했습니다. 잠시 후 다시 시도해주세요."),
    ACCOUNT_VERIFICATION_SUCCESS(true, HttpStatus.OK.value(), "본인 확인이 완료되었습니다. 새 비밀번호를 설정해주세요."),
    // 비밀번호 관련
    PASSWORD_RESET_SUCCESS(true, HttpStatus.OK.value(), "비밀번호가 성공적으로 변경되었습니다."),
    PASSWORD_RESET_FAILED(false, HttpStatus.BAD_REQUEST.value(), "비밀번호 변경에 실패했습니다."),
    INVALID_RESET_TOKEN(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않거나 만료된 토큰입니다."),
    PASSWORD_RESET_EMAIL_SENT(true, HttpStatus.OK.value(), "비밀번호 재설정 이메일이 발송되었습니다."),

    // 마이페이지 정보입력
    INVALID_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "비밀번호가 올바르지 않습니다."),

    // 이메일 인증 관련 (400 BAD_REQUEST 사용)
    EMAIL_SEND_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "이메일 발송에 실패했습니다."),
    INVALID_VERIFICATION_CODE(false, HttpStatus.BAD_REQUEST.value(), "인증번호가 올바르지 않습니다."),
    VERIFICATION_CODE_EXPIRED(false, HttpStatus.BAD_REQUEST.value(), "인증번호가 만료되었습니다."),
    EMAIL_NOT_VERIFIED(false, HttpStatus.BAD_REQUEST.value(), "이메일 인증이 완료되지 않았습니다."),

    // BaseResponseStatus.java에 추가할 OAuth 관련 상태 코드들



    // OAuth 공통 에러
    OAUTH_PROVIDER_NOT_SUPPORTED(false, HttpStatus.BAD_REQUEST.value(), "지원하지 않는 OAuth 제공자입니다."),
    OAUTH_EMAIL_ALREADY_EXISTS(false, HttpStatus.CONFLICT.value(), "해당 이메일은 이미 다른 계정에서 사용 중입니다."),
    OAUTH_USER_ACCESS_DENIED(false, HttpStatus.FORBIDDEN.value(), "소셜 로그인 사용자는 해당 기능을 이용할 수 없습니다."),
    OAUTH_USER_PASSWORD_RESET_DENIED(false, HttpStatus.FORBIDDEN.value(), "소셜 로그인 사용자는 비밀번호 재설정을 할 수 없습니다. 해당 소셜 서비스에서 비밀번호를 변경해주세요."),
    OAUTH_USER_PASSWORD_CHANGE_DENIED(false, HttpStatus.FORBIDDEN.value(), "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다."),
    OAUTH_USER_EMAIL_CHANGE_DENIED(false, HttpStatus.FORBIDDEN.value(), "소셜 로그인 사용자는 이메일을 변경할 수 없습니다."),

    // 카카오 OAuth 관련
    KAKAO_TOKEN_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "카카오 액세스 토큰 요청에 실패했습니다."),
    KAKAO_USER_INFO_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "카카오 사용자 정보 요청에 실패했습니다."),
    KAKAO_TOKEN_EXTRACT_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "카카오 토큰 추출에 실패했습니다."),

    // 네이버 OAuth 에러
    NAVER_TOKEN_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "네이버 토큰 요청에 실패했습니다."),
    NAVER_USER_INFO_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "네이버 사용자 정보 요청에 실패했습니다."),
    NAVER_TOKEN_EXTRACT_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "네이버 토큰 추출에 실패했습니다."),

    // 구글 OAuth 에러
    GOOGLE_TOKEN_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "구글 토큰 요청에 실패했습니다."),
    GOOGLE_USER_INFO_REQUEST_FAILED(false, HttpStatus.BAD_GATEWAY.value(), "구글 사용자 정보 요청에 실패했습니다."),
    GOOGLE_TOKEN_EXTRACT_FAILED(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "구글 토큰 추출에 실패했습니다."),

    // 공통 에러
    DATABASE_CONSTRAINT_ERROR(false, HttpStatus.BAD_REQUEST.value(), "데이터 제약조건 위반입니다."),
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 오류가 발생했습니다."),
    DATABASE_CONNECTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패했습니다."),
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

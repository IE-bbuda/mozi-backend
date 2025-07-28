package org.iebbuda.mozi.exception;


import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)
public class ApiExceptionAdvice {
    /**
     * BaseException 처리 (비즈니스 로직 예외)
     * - BaseResponseStatus의 code를 그대로 사용
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getStatus().getCode())  // BaseResponseStatus의 code 사용
                .body(new BaseResponse<>(e.getStatus()));
    }

    /**
     * 데이터베이스 제약조건 위반 (400 Bad Request)
     * - 중복 키, NOT NULL 제약조건 위반 등
     * - 주로 클라이언트 데이터 문제
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400 - 클라이언트 데이터 문제
                .body(new BaseResponse<>(BaseResponseStatus.DATABASE_CONSTRAINT_ERROR));
    }

    /**
     * 데이터베이스 접근 예외 (500 Internal Server Error)
     * - DB 연결 실패, SQL 문법 오류 등
     * - 서버 인프라 문제
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataAccessException(DataAccessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500 - 서버 문제
                .body(new BaseResponse<>(BaseResponseStatus.DATABASE_ERROR));
    }

    /**
     * 잘못된 요청 파라미터 (400 Bad Request)
     * - 파라미터 형식 오류, 필수값 누락 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400 - 클라이언트 요청 문제
                .body(new BaseResponse<>(false, HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    /**
     * 예상치 못한 예외 (500 Internal Server Error)
     * - 모든 예외의 최종 처리자
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500 - 예상치 못한 서버 오류
                .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(false, 400,
                        String.format("필수 파라미터가 누락되었습니다: %s", e.getParameterName())));
    }
}

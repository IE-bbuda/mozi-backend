package org.iebbuda.mozi.exception;


import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Order(2)
public class ApiExceptionAdvice {
    /**
     * BaseException 처리 (비즈니스 로직 예외)
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
        return ResponseEntity.ok(new BaseResponse<>(e.getStatus()));
    }

    /**
     * 일반적인 Exception 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * 데이터베이스 제약조건 위반
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.DATABASE_INSERT_ERROR));
    }
    /**
     * 잘못된 요청 파라미터
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.ok(new BaseResponse<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        ));
    }
}

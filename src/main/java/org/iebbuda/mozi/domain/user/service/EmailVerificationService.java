package org.iebbuda.mozi.domain.user.service;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.iebbuda.mozi.common.response.BaseResponseStatus;

import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
@PropertySource("classpath:application.properties")
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final ConcurrentHashMap<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, VerifiedEmail> verifiedEmails = new ConcurrentHashMap<>(); // 인증 완료된 이메일

    @Value("${mail.username}")
    private String fromEmail;

    /**
     * PasswordResetService에서 사용하는 기존 메서드 (비밀번호 재설정용)
     * @param email 인증번호를 받을 이메일
     */
    public void sendVerificationCode(String email) {
        sendVerificationCode(email, "password");
    }

    /**
     * 회원가입용 인증번호 발송
     * @param email 인증번호를 받을 이메일
     */
    public void sendSignupVerificationCode(String email) {
        sendVerificationCode(email, "signup");
    }

    /**
     * 마이페이지 이메일 변경용 인증번호 발송
     * @param email 인증번호를 받을 이메일
     */
    public void sendMyPageVerificationCode(String email) {
        sendVerificationCode(email, "mypage");
    }

    /**
     * 인증번호 발송 (내부 메서드)
     * @param email 인증번호를 받을 이메일
     * @param purpose 용도 ("signup" 또는 "password")
     */
    private void sendVerificationCode(String email, String purpose) {
        String code = generateVerificationCode();

        // 5분 후 만료
        VerificationCode verificationCode = new VerificationCode(code, System.currentTimeMillis() + 300000, purpose);
        verificationCodes.put(email, verificationCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);

            // 용도에 따라 제목과 내용 변경
            switch (purpose) {
                case "signup":
                    helper.setSubject("[MoZi] 회원가입 이메일 인증번호");
                    helper.setText(createSignupEmailContent(code), true);
                    break;
                case "password":
                    helper.setSubject("[MoZi] 비밀번호 재설정 인증번호");
                    helper.setText(createPasswordResetEmailContent(code), true);
                    break;
                case "mypage":
                    helper.setSubject("[MoZi] 이메일 변경 인증번호");
                    helper.setText(createMyPageEmailContent(code), true);
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 인증 용도입니다: " + purpose);
            }

            mailSender.send(message);
            log.info("인증번호 발송 완료 - 이메일: {}, 용도: {}", email, purpose);

        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage());
            throw new BaseException(BaseResponseStatus.EMAIL_SEND_FAILED);
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        VerificationCode saved = verificationCodes.get(email);

        if (saved == null) {
            log.warn("인증번호 없음 - 이메일: {}", email);
            return false;
        }

        if (saved.isExpired()) {
            log.warn("인증번호 만료 - 이메일: {}", email);
            verificationCodes.remove(email);
            return false;
        }

        if (!saved.getCode().equals(inputCode)) {
            log.warn("인증번호 불일치 - 이메일: {}", email);
            return false;
        }

        // 회원가입용 인증인 경우 인증 완료 상태 저장 (30분간 유효)
//        if ("signup".equals(saved.getPurpose())) {
            VerifiedEmail verifiedEmail = new VerifiedEmail(email, System.currentTimeMillis() + 1800000); // 30분
            verifiedEmails.put(email, verifiedEmail);
            log.info("회원가입용 이메일 인증 완료 상태 저장 - 이메일: {}", email);
//        }

        verificationCodes.remove(email); // 인증번호만 삭제 (인증 상태는 유지)
        log.info("인증번호 확인 성공 - 이메일: {}, 용도: {}", email, saved.getPurpose());
        return true;
    }

    /**
     * 이메일이 인증되었는지 확인 (회원가입용)
     * @param email 확인할 이메일
     * @return 인증 여부
     */
    public boolean isEmailVerified(String email) {
        VerifiedEmail verified = verifiedEmails.get(email);

        if (verified == null) {
            return false;
        }

        if (verified.isExpired()) {
            verifiedEmails.remove(email);
            log.warn("이메일 인증 상태 만료 - 이메일: {}", email);
            return false;
        }

        return true;
    }

    /**
     * 회원가입 완료 시 인증 상태 제거
     * @param email 제거할 이메일
     */
    public void clearVerifiedStatus(String email) {
        verifiedEmails.remove(email);
        log.info("이메일 인증 상태 제거 - 이메일: {}", email);
    }

    /**
     * 회원가입용 이메일 내용 생성
     */
    private String createSignupEmailContent(String code) {
        return createEmailTemplate(
                "회원가입 이메일 인증",
                "MoZi 회원가입을 위한 이메일 인증번호를 보내드립니다.",
                code
        );
    }

    /**
     * 비밀번호 재설정용 이메일 내용 생성
     */
    private String createPasswordResetEmailContent(String code) {
        return createEmailTemplate(
                "비밀번호 재설정",
                "비밀번호 재설정을 위한 인증번호를 보내드립니다.",
                code
        );
    }

    /**
     * 마이페이지 이메일 변경용 이메일 내용 생성
     */
    private String createMyPageEmailContent(String code) {
        return createEmailTemplate(
                "이메일 변경 인증",
                "이메일 주소 변경을 위한 인증번호를 보내드립니다.",
                code
        );
    }

    /**
     * 공통 이메일 템플릿 생성
     */
    private String createEmailTemplate(String title, String description, String code) {
        return "<div style='padding: 20px; font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #36c18c; margin: 0;'>MoZi</h1>" +
                "<h2 style='color: #333; margin: 10px 0;'>" + title + "</h2>" +
                "</div>" +

                "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                "<p style='margin: 0 0 15px 0; color: #333;'>안녕하세요!</p>" +
                "<p style='margin: 0 0 15px 0; color: #333;'>" + description + "</p>" +
                "</div>" +

                "<div style='background: #e3f2fd; padding: 20px; margin: 20px 0; text-align: center; border-radius: 8px; border-left: 4px solid #2196f3;'>" +
                "<h3 style='margin: 0 0 10px 0; color: #1976d2;'>인증번호</h3>" +
                "<div style='font-size: 36px; font-weight: bold; color: #1976d2; letter-spacing: 4px;'>" +
                code +
                "</div>" +
                "</div>" +

                "<div style='background: #fff3e0; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<p style='margin: 0; color: #e65100; font-size: 14px;'>" +
                "⚠️ <strong>주의사항</strong></p>" +
                "<ul style='margin: 10px 0 0 0; color: #bf360c; font-size: 14px;'>" +
                "<li>인증번호는 <strong>5분 내에</strong> 입력해주세요.</li>" +
                "<li>본인이 요청하지 않았다면 이 메일을 무시해주세요.</li>" +
                "<li>인증번호를 타인에게 알려주지 마세요.</li>" +
                "</ul>" +
                "</div>" +

                "<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;'>" +
                "<p style='margin: 0; color: #666; font-size: 12px;'>© 2025 MoZi. All rights reserved.</p>" +
                "</div>" +
                "</div>";
    }
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // 기존 EmailVerificationService 마지막에 이 메서드만 추가
    /**
     * 테스트용: 회원가입 이메일 인증 상태를 직접 설정
     * @param email 인증 상태로 설정할 이메일
     */
    public void setEmailVerifiedForTest(String email) {
        VerifiedEmail verified = new VerifiedEmail(email, System.currentTimeMillis() + 1800000); // 30분
        verifiedEmails.put(email, verified);
        log.info("테스트용 이메일 인증 상태 설정 - 이메일: {}", email);
    }

    // 내부 클래스들
    @Data
    @AllArgsConstructor
    private static class VerificationCode {
        private String code;
        private long expireTime;
        private String purpose; // "signup" 또는 "password"

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    @Data
    @AllArgsConstructor
    private static class VerifiedEmail {
        private String email;
        private long expireTime;

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
}

package com.example.ranking.infra.auth.service;

import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.global.util.MailSender;
import com.example.ranking.global.util.VerificationCodeGenerator;
import com.example.ranking.infra.persistence.user.UserEmailAuthEntity;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UserEmailAuthJpaRepository;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMailAuthService {

    private final MailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserEmailAuthJpaRepository userEmailAuthJpaRepository;
    private static final String EMAIL_AUTH_SUBJECT = "[Quiz] 인증번호를 확인해주세요.";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_RETRIES = 10;
    private final UsersJpaRepository usersJpaRepository;

    @Value("${spring.mail.expirationtime.minutes}")
    private int expirationMinutes;

    public void sendEmailAuthVerificationCode(String email){

        Optional<UsersEntity> optionalUsersEntity = usersJpaRepository.findUserEntityByEmail(email);

        if (optionalUsersEntity.isPresent()){
            throw new QuizException(ErrorCode.USER_ALREADY_EXISTS);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = currentTime.plusMinutes(expirationMinutes);
        String verificationCode = createEmailAuthVerificationCode(currentTime);

        userEmailAuthJpaRepository.save(
                UserEmailAuthEntity.builder()
                        .code(verificationCode)
                        .createdAt(currentTime)
                        .expiratedAt(expirationTime)
                        .build()
        );

        mailSender.sendHtmlEmail(email, EMAIL_AUTH_SUBJECT, createHtmlMailAuthContext(expirationTime, verificationCode));

    }

    private String createEmailAuthVerificationCode(LocalDateTime currentTime) {

        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = verificationCodeGenerator.generateArithmeticVerificationCode(CODE_LENGTH);
            boolean exists = userEmailAuthJpaRepository
                    .findByCodeAndExpiratedAtAfter(code, currentTime)
                    .isPresent();

            if (!exists) {
                return code;
            }
        }

        throw new IllegalStateException("Failed to generate unique verification code after " + MAX_RETRIES + " attempts");
    }

    private String createHtmlMailAuthContext(LocalDateTime expirationTime, String verificationCode) {
        Context context = new Context();
        context.setVariable("expirationMinutes", expirationMinutes);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("expirationTime", expirationTime);
        return templateEngine.process("email-auth", context);
    }

    @Transactional
    public void verifyEmailAuthCode(String verificationCode) {

        verifyIfVerificationCodeLengthMatching(verificationCode, CODE_LENGTH);
        verifyIfVerificationCodeArithmetic(verificationCode);

        UserEmailAuthEntity userEmailAuthEntity = userEmailAuthJpaRepository
                .findByCode(verificationCode)
                .orElseThrow(() -> new QuizException(ErrorCode.INVALID_EMAIL_CODE));

        verifyIfVerificationCodeAlreadyExpired(userEmailAuthEntity);

        userEmailAuthEntity.reset();

    }

    private void verifyIfVerificationCodeLengthMatching(String verificationCode, int digitLength){
        if (verificationCode.length() != digitLength) {
            log.info("VerificationCode Length is not matching --> current {} / should-be {}", verificationCode.length(), digitLength);
            throw new QuizException(ErrorCode.INVALID_EMAIL_CODE);
        }
    }

    private void verifyIfVerificationCodeArithmetic(String verificationCode) {

        for (int i = 0; i < verificationCode.length(); i++) {
            if (!(verificationCode.charAt(i) >= '0' && verificationCode.charAt(i) <= '9')) {
                log.info("VerificationCode is not Arithmetic String");
                throw new QuizException(ErrorCode.INVALID_EMAIL_CODE);
            }
        }

    }

    private void verifyIfVerificationCodeAlreadyExpired(UserEmailAuthEntity userEmailAuthEntity) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (userEmailAuthEntity.getExpiratedAt().isBefore(currentTime)) {
            log.info(">>> 유효 기간이 만료된 코드입니다. {}", userEmailAuthEntity.getCode());
            throw new QuizException(ErrorCode.EMAIL_CODE_EXPIRED);
        }
    }

}

package com.example.ranking.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
@Slf4j
public class VerificationCodeGenerator {

    public String generateArithmeticVerificationCode(int digitLength){
        SecureRandom secureRandom = new SecureRandom();
        int lowerLimit = (int) Math.pow(10, digitLength - 1); // 100000
        int upperLimit = (int) Math.pow(10, digitLength);     // 1000000
        int randomNum = secureRandom.nextInt(upperLimit - lowerLimit) + lowerLimit;
        return String.valueOf(randomNum);
    }

}

package com.sgu.student_admission_system.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordUtil {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL = LOWER + UPPER + DIGIT + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(8);

        // đảm bảo mỗi loại có ít nhất 1 ký tự
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(DIGIT.charAt(random.nextInt(DIGIT.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // fill đủ 8 ký tự
        for (int i = 4; i < 8; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        // shuffle để không bị predictable
        return shuffle(password.toString());
    }

    private static String shuffle(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }

}

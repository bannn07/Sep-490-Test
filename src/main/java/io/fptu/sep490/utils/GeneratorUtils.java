package io.fptu.sep490.utils;

import java.util.Random;

public class GeneratorUtils {

    public static String generateRandomOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        char[] pin = new char[length];

        for (int i = 0; i < length; i++) {
            pin[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        return new String(pin);
    }

}

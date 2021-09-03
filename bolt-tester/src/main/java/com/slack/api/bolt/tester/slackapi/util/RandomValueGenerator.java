package com.slack.api.bolt.tester.slackapi.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;

public class RandomValueGenerator {
    private RandomValueGenerator() {}

    public static String generateAlphanumericString(int length) {
        return UUID.randomUUID().toString()
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("-", "")
                .substring(0, length);
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] NUMBERS = "0123456789".toCharArray();

    public static String generateNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(NUMBERS.length);
            sb.append(NUMBERS[idx]);
        }
        return sb.toString();
    }

    public static String generateSlackIDString(String prefix, int length) {
        return prefix + UUID.randomUUID().toString()
                .toUpperCase(Locale.ENGLISH)
                .replaceAll("-", "")
                .substring(0, length - 1);
    }
}

package com.davwards.elementals;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    public static String randomString(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    public static int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }
}

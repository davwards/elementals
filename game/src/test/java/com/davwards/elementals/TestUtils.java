package com.davwards.elementals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class TestUtils {
    public static String randomString(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    public static int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static <T extends Comparable> void assertThatValueIncreases(Supplier<T> getValue, Runnable event) {
        T originalValue = getValue.get();
        event.run();
        assertThat(getValue.get(), greaterThan(originalValue));
    }

    public static <T extends Comparable> void assertThatValueDecreases(Supplier<T> getValue, Runnable event) {
        T originalValue = getValue.get();
        event.run();
        assertThat(getValue.get(), lessThan(originalValue));
    }

    public static <T> void assertThatValueChanges(Supplier<T> getValue, T start, T end, Runnable event) {
        assertThat(getValue.get(), equalTo(start));
        event.run();
        assertThat(getValue.get(), equalTo(end));
    }

    public static <T> void assertThatValueDoesNotChange(Supplier<T> getValue, Runnable event) {
        T originalValue = getValue.get();
        event.run();
        assertThat(getValue.get(), equalTo(originalValue));
    }

    public static void assertThatValuesDoNotChange(Runnable event, Supplier... getters) {
        List<Object> originalValues = Arrays.stream(getters).map(Supplier::get).collect(Collectors.toList());
        event.run();
        List<Object> finalValues = Arrays.stream(getters).map(Supplier::get).collect(Collectors.toList());
        assertThat(finalValues, equalTo(originalValues));
    }
}

package com.davwards.elementals;

import com.davwards.elementals.game.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedTask;
import com.davwards.elementals.game.tasks.models.Task;

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

    public static ImmutableUnsavedPlayer randomUnsavedPlayer() {
        return ImmutableUnsavedPlayer.builder()
                .name("Test Player " + randomString(10))
                .experience(randomInt(0, 100))
                .health(randomInt(1,100))
                .build();
    }

    public static ImmutableUnsavedTask randomUnsavedTask() {
        return ImmutableUnsavedTask.builder()
                .title("Test Task " + randomString(10))
                .playerId(new PlayerId(randomString(10)))
                .status(Task.Status.INCOMPLETE)
                .build();
    }

    public static <T> ValueAssertion<T> assertThatValue(Supplier<T> supplier) {
        return new ValueAssertion<>(supplier);
    }

    public static MultipleValuesAssertion assertThatValues(Supplier ...suppliers) {
        return new MultipleValuesAssertion(suppliers);
    }

    public static IntegerValueAssertion assertThatInteger(Supplier<Integer> getValue) {
        return new IntegerValueAssertion(getValue);
    }

    public static class ValueAssertion<T> {
        final Supplier<T> getValue;

        private ValueAssertion(Supplier<T> supplier) {
            this.getValue = supplier;
        }

        public void doesNotChangeWhen(Runnable event) {
            T originalValue = getValue.get();
            event.run();
            assertThat(getValue.get(), equalTo(originalValue));
        }

        public ValueChangeAssertion<T> changesFrom(T start, T end) {
            return new ValueChangeAssertion<>(getValue, start, end);
        }
    }

    public static class MultipleValuesAssertion {
        final Supplier[] getters;

        private MultipleValuesAssertion(Supplier ...getters) {
            this.getters = getters;
        }

        public void doNotChangeWhen(Runnable event) {
            List<Object> originalValues = Arrays.stream(getters)
                    .map(Supplier::get)
                    .collect(Collectors.toList());

            event.run();

            List<Object> finalValues = Arrays.stream(getters)
                    .map(Supplier::get)
                    .collect(Collectors.toList());

            assertThat(finalValues, equalTo(originalValues));
        }
    }

    public static class IntegerValueAssertion extends ValueAssertion<Integer> {

        private IntegerValueAssertion(Supplier<Integer> getValue) {
            super(getValue);
        }

        public void increasesWhen(Runnable event) {
            Integer originalValue = getValue.get();
            event.run();
            assertThat(getValue.get(), greaterThan(originalValue));
        }

        public void decreasesWhen(Runnable event) {
            Integer originalValue = getValue.get();
            event.run();
            assertThat(getValue.get(), lessThan(originalValue));
        }
    }

    public static class ValueChangeAssertion<T> {

        private final Supplier<T> getValue;
        private final T start;
        private final T end;

        private ValueChangeAssertion(Supplier<T> getValue, T start, T end) {
            this.getValue = getValue;
            this.start = start;
            this.end = end;
        }

        public void when(Runnable event) {
            assertThat(getValue.get(), equalTo(start));
            event.run();
            assertThat(getValue.get(), equalTo(end));
        }
    }
}

package com.davwards.elementals.game.support.test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class Assertions {
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

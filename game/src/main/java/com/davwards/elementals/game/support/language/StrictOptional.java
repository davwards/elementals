package com.davwards.elementals.game.support.language;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface StrictOptional<T> {
    <U> StrictOptional<U> map(Function<T, U> fn);
    <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn);
    StrictOptional<T> filter(Predicate<T> test);
    T orElse(T defaultValue);
    T orElseGet(Supplier<T> supplier);

    static <U> StrictOptional<U> of(U value) {
        return value == null
                ? new StrictNone<>()
                : new StrictJust<>(value);
    }

    static <U> StrictOptional<U> strict(Optional<U> optional) {
        return StrictOptional.of(optional.orElse(null));
    }

    static <U> StrictOptional<U> empty() {
        return new StrictNone<>();
    }

    class StrictJust<T> implements StrictOptional<T> {
        private T value;
        private StrictJust(T value) {
            this.value = value;
        }

        @Override
        public <U> StrictOptional<U> map(Function<T, U> fn) {
            return new StrictJust<>(fn.apply(value));
        }

        @Override
        public <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn) {
            return fn.apply(value);
        }

        @Override
        public StrictOptional<T> filter(Predicate<T> test) {
            return test.test(value)
                    ? this
                    : new StrictNone<>();
        }

        @Override
        public T orElse(T defaultValue) {
            return value;
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return value;
        }
    }

    class StrictNone<T> implements StrictOptional<T> {
        @Override
        public <U> StrictOptional<U> map(Function<T, U> fn) {
            return new StrictNone<>();
        }

        @Override
        public <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn) {
            return new StrictNone<>();
        }

        @Override
        public StrictOptional<T> filter(Predicate<T> test) {
            return new StrictNone<>();
        }

        @Override
        public T orElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return supplier.get();
        }
    }
}

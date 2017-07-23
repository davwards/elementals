package com.davwards.elementals.support.language;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* This is a less "friendly" implementation of Optional than the
 * standard one in java.util. The standard Optional will helpfully
 * become empty if a function passed to `map` returns null. Most
 * of the time that's helpful behavior, but on many occasions I
 * need an optional to behave more strictly.
 *
 * StrictOptional doesn't handle any special cases for you. If you
 * map a function that returns null, you'll have a Strict.Just
 * containing null. If you don't want that, use `flatMap`.
 */

public interface StrictOptional<T> {
    <U> StrictOptional<U> map(Function<T, U> fn);
    <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn);
    StrictOptional<T> filter(Predicate<T> test);
    T orElse(T defaultValue);
    T orElseGet(Supplier<T> supplier);
    T orElseThrow(Supplier<RuntimeException> boom);

    static <U> StrictOptional<U> of(U value) {
        return value == null
                ? new None<>()
                : new Just<>(value);
    }

    static <U> StrictOptional<U> strict(Optional<U> optional) {
        return StrictOptional.of(optional.orElse(null));
    }

    static <U> StrictOptional<U> empty() {
        return new None<>();
    }

    class Just<T> implements StrictOptional<T> {
        private T value;
        private Just(T value) {
            this.value = value;
        }

        @Override
        public <U> StrictOptional<U> map(Function<T, U> fn) {
            return new Just<>(fn.apply(value));
        }

        @Override
        public <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn) {
            return fn.apply(value);
        }

        @Override
        public StrictOptional<T> filter(Predicate<T> test) {
            return test.test(value)
                    ? this
                    : new None<>();
        }

        @Override
        public T orElse(T defaultValue) {
            return value;
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return value;
        }

        @Override
        public T orElseThrow(Supplier<RuntimeException> boom) {
            return value;
        }
    }

    class None<T> implements StrictOptional<T> {
        private None() {}

        @Override
        public <U> StrictOptional<U> map(Function<T, U> fn) {
            return new None<>();
        }

        @Override
        public <U> StrictOptional<U> flatMap(Function<T, StrictOptional<U>> fn) {
            return new None<>();
        }

        @Override
        public StrictOptional<T> filter(Predicate<T> test) {
            return new None<>();
        }

        @Override
        public T orElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return supplier.get();
        }

        @Override
        public T orElseThrow(Supplier<RuntimeException> boom) {
            throw boom.get();
        }
    }
}

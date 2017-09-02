package com.davwards.elementals.support.language;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Either<S, F> {
    <T> Either<T, F> map(Function<S, T> fn);

    S orIfFailure(Function<F, S> fn);

    static <S, F> Either<S, F> success(S value) {
        return new Success<>(value);
    }

    static <S, F> Either<S, F> failure(F value) {
        return new Failure<>(value);
    }

    static Either success() {
        return new Success<Void, Void>(null);
    }

    static Either failure() {
        return new Failure<Void, Void>(null);
    }

    S orIfFailureThrow(Supplier<RuntimeException> getThrowable);

    class Success<S, F> implements Either<S, F> {
        private final S value;

        private Success(S value) {
            this.value = value;
        }

        @Override
        public <T> Either<T, F> map(Function<S, T> fn) {
            return new Success<>(fn.apply(value));
        }

        @Override
        public S orIfFailure(Function<F, S> fn) {
            return value;
        }

        @Override
        public S orIfFailureThrow(Supplier<RuntimeException> getThrowable) {
            return value;
        }
    }

    class Failure<S, F> implements Either<S, F> {
        private final F value;

        private Failure(F value) {
            this.value = value;
        }

        @Override
        public <T> Either<T, F> map(Function<S, T> fn) {
            return new Failure<>(value);
        }

        @Override
        public S orIfFailure(Function<F, S> fn) {
            return fn.apply(value);
        }

        @Override
        public S orIfFailureThrow(Supplier<RuntimeException> getThrowable) {
            throw getThrowable.get();
        }
    }
}

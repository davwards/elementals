package com.davwards.elementals.game.support.language;

import java.util.function.Function;

public interface Either<S, F> {
    <T> Either<T, F> mapSuccess(Function<S, T> fn);

    <T> Either<S, T> mapFailure(Function<F, T> fn);

    <T> T join(Function<S, T> ifSuccess, Function<F, T> ifFailure);

    static <S, F> Success<S, F> success(S value) {
        return new Success<>(value);
    }

    static <S, F> Failure<S, F> failure(F value) {
        return new Failure<>(value);
    }

    class Success<S, F> implements Either<S, F> {
        private final S value;

        private Success(S value) {
            this.value = value;
        }

        @Override
        public <T> Either<T, F> mapSuccess(Function<S, T> fn) {
            return new Success<>(fn.apply(value));
        }

        @Override
        public <T> Either<S, T> mapFailure(Function<F, T> fn) {
            return new Success<>(value);
        }

        @Override
        public <T> T join(Function<S, T> ifSuccess, Function<F, T> ifFailure) {
            return ifSuccess.apply(value);
        }
    }

    class Failure<S, F> implements Either<S, F> {
        private final F value;

        private Failure(F value) {
            this.value = value;
        }

        @Override
        public <T> Either<T, F> mapSuccess(Function<S, T> fn) {
            return new Failure<>(value);
        }

        @Override
        public <T> Either<S, T> mapFailure(Function<F, T> fn) {
            return new Failure<>(fn.apply(value));
        }

        @Override
        public <T> T join(Function<S, T> ifSuccess, Function<F, T> ifFailure) {
            return ifFailure.apply(value);
        }
    }
}

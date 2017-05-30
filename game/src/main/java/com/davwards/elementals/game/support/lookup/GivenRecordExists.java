package com.davwards.elementals.game.support.lookup;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/* This helper is subtly different than using a plain Optional.
 * See its test for details. */
public class GivenRecordExists {
    public static <I, O> PresenceHandled<I, O> givenRecordExists(
            Optional<I> record,
            Function<I, O> ifPresent) {
        return new PresenceHandled<>(record, ifPresent);
    }

    public static class PresenceHandled<I, O> {
        final private Optional<I> record;
        final private Function<I, O> ifPresent;

        PresenceHandled(Optional<I> record, Function<I, O> ifPresent) {
            this.record = record;
            this.ifPresent = ifPresent;
        }

        public O otherwise(Supplier<O> ifNotPresent) {
            if (!record.isPresent()) return ifNotPresent.get();
            return ifPresent.apply(record.get());
        }
    }
}

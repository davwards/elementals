package com.davwards.elementals.game.habits.persistence;

import com.davwards.elementals.game.habits.models.*;
import com.davwards.elementals.game.support.persistence.CrudRepositoryTest;

import static com.davwards.elementals.game.support.test.Factories.randomUnsavedHabit;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public abstract class HabitRepositoryTest extends CrudRepositoryTest<HabitRepository, HabitId, UnsavedHabit, SavedHabit>{

    @Override
    protected UnsavedHabit givenAnUnsavedRecord() {
        return randomUnsavedHabit();
    }

    @Override
    protected SavedHabit whenASavedRecordIsModified(SavedHabit original) {
        return ImmutableSavedHabit.copyOf(original)
                .withTitle("Modified " + original.title());
    }

    @Override
    protected void assertIdentical(UnsavedHabit original, SavedHabit saved) {
        assertThat(
                original,
                equalTo(ImmutableUnsavedHabit.builder().from(saved).build())
        );
    }

    @Override
    protected void assertIdentical(SavedHabit original, SavedHabit saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedHabit left, SavedHabit right) {
        assertThat(left, not(equalTo(right)));
    }
}
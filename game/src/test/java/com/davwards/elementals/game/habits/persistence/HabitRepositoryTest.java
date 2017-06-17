package com.davwards.elementals.game.habits.persistence;

import com.davwards.elementals.game.habits.models.*;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.CrudRepositoryTest;
import org.junit.Test;

import java.util.List;

import static com.davwards.elementals.game.support.test.Factories.randomUnsavedHabit;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public abstract class HabitRepositoryTest extends CrudRepositoryTest<HabitRepository, HabitId, UnsavedHabit, SavedHabit>{

    @Test
    public void findByPlayer() throws Exception {
        PlayerId matchingPlayerId = new PlayerId("matching");
        PlayerId nonmatchingPlayerId = new PlayerId("nonmatching");

        SavedHabit matchingHabit1 = repository().save(randomUnsavedHabit()
                .withPlayerId(matchingPlayerId));
        SavedHabit matchingHabit2 = repository().save(randomUnsavedHabit()
                .withPlayerId(matchingPlayerId));
        SavedHabit nonmatchingHabit = repository().save(randomUnsavedHabit()
                .withPlayerId(nonmatchingPlayerId));

        List<SavedHabit> results = repository().findByPlayerId(matchingPlayerId);

        assertThat(results, hasItem(matchingHabit1));
        assertThat(results, hasItem(matchingHabit2));
        assertThat(results, not(hasItem(nonmatchingHabit)));
    }


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
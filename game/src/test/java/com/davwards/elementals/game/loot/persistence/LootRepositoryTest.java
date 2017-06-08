package com.davwards.elementals.game.loot.persistence;

import com.davwards.elementals.game.loot.models.*;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.CrudRepositoryTest;

import static com.davwards.elementals.game.support.test.Factories.randomString;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedLoot;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class LootRepositoryTest extends CrudRepositoryTest<LootRepository, LootId, UnsavedLoot, SavedLoot> {

    @Override
    protected UnsavedLoot givenAnUnsavedRecord() {
        return randomUnsavedLoot();
    }

    @Override
    protected SavedLoot whenASavedRecordIsModified(SavedLoot original) {
        return ImmutableSavedLoot.copyOf(original)
                .withPlayerId(new PlayerId(randomString(10)));
    }

    @Override
    protected void assertIdentical(UnsavedLoot original, SavedLoot saved) {
        assertThat(ImmutableUnsavedLoot.builder().from(saved).build(), equalTo(original));
    }

    @Override
    protected void assertIdentical(SavedLoot original, SavedLoot saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedLoot left, SavedLoot right) {
        assertThat(left, not(equalTo(right)));
    }
}

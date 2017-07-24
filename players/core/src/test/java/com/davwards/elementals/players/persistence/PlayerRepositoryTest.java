package com.davwards.elementals.players.persistence;

import com.davwards.elementals.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.models.UnsavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;
import com.davwards.elementals.support.persistence.CrudRepositoryTest;

import static com.davwards.elementals.support.test.Factories.randomString;
import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public abstract class PlayerRepositoryTest extends CrudRepositoryTest<PlayerRepository, PlayerId, UnsavedPlayer, SavedPlayer> {
    @Override
    protected UnsavedPlayer givenAnUnsavedRecord() {
        return randomUnsavedPlayer();
    }

    @Override
    protected SavedPlayer whenASavedRecordIsModified(SavedPlayer original) {
        return SavedPlayer.copy(original)
                .withName("Modified Player " + randomString(5))
                .withHealth(original.health()-1);
    }

    @Override
    protected void assertIdentical(UnsavedPlayer original, SavedPlayer saved) {
        assertThat(original, equalTo(ImmutableUnsavedPlayer.builder().from(saved).build()));
    }

    @Override
    protected void assertIdentical(SavedPlayer original, SavedPlayer saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedPlayer left, SavedPlayer right) {
        assertThat(left, not(equalTo(right)));
    }
}
package com.davwards.elementals.game.players.persistence;

import com.davwards.elementals.game.entities.CrudRepositoryTest;
import com.davwards.elementals.game.players.*;
import com.davwards.elementals.game.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.models.UnsavedPlayer;

import static com.davwards.elementals.TestUtils.randomString;
import static com.davwards.elementals.TestUtils.randomUnsavedPlayer;
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
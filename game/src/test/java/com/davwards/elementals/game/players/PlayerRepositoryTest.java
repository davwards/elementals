package com.davwards.elementals.game.players;

import com.davwards.elementals.game.CrudRepositoryTest;

import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public abstract class PlayerRepositoryTest extends CrudRepositoryTest<PlayerRepository, PlayerId, UnsavedPlayer, SavedPlayer> {
    @Override
    protected UnsavedPlayer givenAnUnsavedRecord() {
        return ImmutableUnsavedPlayer.builder()
                .name("Player " + randomString(5))
                .experience(100)
                .health(50)
                .build();
    }

    @Override
    protected SavedPlayer whenASavedRecordIsModified(SavedPlayer original) {
        return ImmutableSavedPlayer
                .copyOf(original)
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
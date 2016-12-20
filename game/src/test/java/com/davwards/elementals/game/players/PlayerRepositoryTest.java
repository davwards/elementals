package com.davwards.elementals.game.players;

import com.davwards.elementals.game.CrudRepository;
import com.davwards.elementals.game.CrudRepositoryTest;

import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public abstract class PlayerRepositoryTest extends CrudRepositoryTest<PlayerRepository, PlayerId, UnsavedPlayer, SavedPlayer> {
    @Override
    protected UnsavedPlayer givenAnUnsavedRecord() {
        return new UnsavedPlayer("Player " + randomString(5));
    }

    @Override
    protected void whenASavedRecordIsModified(SavedPlayer original) {
        original.setName("Modified Player " + randomString(5));
    }

    @Override
    protected void assertIdentical(UnsavedPlayer original, SavedPlayer saved) {
        assertThat(original.getName(), equalTo(saved.getName()));
    }

    @Override
    protected void assertIdentical(SavedPlayer original, SavedPlayer saved) {
        assertThat(original.getName(), equalTo(saved.getName()));
    }

    @Override
    protected void assertNotIdentical(SavedPlayer left, SavedPlayer right) {
        assertThat(left.getName(), not(equalTo(right.getName())));
    }
}
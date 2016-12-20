package com.davwards.elementals.game.players;

import com.davwards.elementals.game.CrudRepositoryTest;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;

import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

public abstract class PlayerRepositoryTest extends CrudRepositoryTest<PlayerRepository, PlayerId, UnsavedPlayer, SavedPlayer> {
    @Override
    protected UnsavedPlayer givenAnUnsavedRecord() {
        return new UnsavedPlayer("Player " + randomString(5));
    }

    @Override
    protected void whenASavedRecordIsModified(SavedPlayer original) {
        original.setName("Modified Player " + randomString(5));
        original.decreaseHealth(1);
    }

    @Override
    protected void assertIdentical(UnsavedPlayer original, SavedPlayer saved) {
        assertThat(original.getName(), equalTo(saved.getName()));
        assertThat(original.getHealth(), equalTo(saved.getHealth()));
    }

    @Override
    protected void assertIdentical(SavedPlayer original, SavedPlayer saved) {
        assertThat(original.getName(), equalTo(saved.getName()));
        assertThat(original.getHealth(), equalTo(saved.getHealth()));
    }

    @Override
    protected void assertNotIdentical(SavedPlayer left, SavedPlayer right) {
        assertThat(left, not(allOf(
                hasProperty("name", equalTo(right.getName())),
                hasProperty("health", equalTo(right.getHealth()))
        )));
    }
}
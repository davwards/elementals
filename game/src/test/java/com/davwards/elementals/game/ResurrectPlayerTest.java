package com.davwards.elementals.game;


import com.davwards.elementals.game.exceptions.NoSuchPlayerException;
import com.davwards.elementals.game.fakes.FakeNotifier;
import com.davwards.elementals.game.fakes.InMemoryPlayerRepository;
import com.davwards.elementals.game.notification.Notification;
import com.davwards.elementals.game.players.*;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatValues;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ResurrectPlayerTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private FakeNotifier notifier = new FakeNotifier();
    private ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, notifier);

    private SavedPlayer deadPlayer = playerRepository.save(ImmutableUnsavedPlayer.builder()
            .name("dead")
            .experience(100)
            .health(0)
            .build());

    private SavedPlayer veryDeadPlayer = playerRepository.save(ImmutableUnsavedPlayer.builder()
            .name("very dead")
            .experience(100)
            .health(-10)
            .build());

    private SavedPlayer alivePlayer = playerRepository.save(ImmutableUnsavedPlayer.builder()
            .name("alive")
            .experience(100)
            .health(10)
            .build());

    @Test
    public void whenPlayerIsAlive_doesNothing() throws Exception {
        assertThatValues(
                () -> playerRepository.find(alivePlayer.getId()).get().experience(),
                () -> playerRepository.find(alivePlayer.getId()).get().health()
        ).doNotChangeWhen(
                () -> resurrectPlayer.perform(alivePlayer.getId())
        );
    }

    @Test
    public void whenPlayerIsDead_clearsExperience() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(playerRepository.find(deadPlayer.getId()).get().experience(), equalTo(0));

        resurrectPlayer.perform(veryDeadPlayer.getId());
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().experience(), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_restoresHealth() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(playerRepository.find(deadPlayer.getId()).get().health(), equalTo(GameConstants.STARTING_HEALTH));

        resurrectPlayer.perform(veryDeadPlayer.getId());
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().health(), equalTo(GameConstants.STARTING_HEALTH));
    }

    @Test
    public void whenPlayerIsDead_sendsNotification() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(notifier.notificationsSent().get(0).getPlayerId(), equalTo(deadPlayer.getId()));
        assertThat(notifier.notificationsSent().get(0).getType(), equalTo(Notification.NotificationType.PLAYER_HAS_DIED));
    }

    @Test
    public void whenPlayerDoesNotExist_throwsException() {
        try {
            resurrectPlayer.perform(new PlayerId("no-such-id"));
            fail("Expected a NoSuchPlayerException to be thrown");
        } catch (NoSuchPlayerException e) {
            assertThat(e.getPlayerId(), equalTo(new PlayerId("no-such-id")));
        }
    }
}
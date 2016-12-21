package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;
import com.davwards.elementals.game.fakeplugins.FakeNotifier;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.notification.Notification;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatValuesDoNotChange;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ResurrectPlayerTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private FakeNotifier notifier = new FakeNotifier();
    private ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, notifier);

    private SavedPlayer deadPlayer = playerRepository.save(new UnsavedPlayer("dead", 100, 0));
    private SavedPlayer veryDeadPlayer = playerRepository.save(new UnsavedPlayer("very dead", 100, -10));
    private SavedPlayer alivePlayer = playerRepository.save(new UnsavedPlayer("alive", 100, 10));

    @Test
    public void whenPlayerIsAlive_doesNothing() throws Exception {
        assertThatValuesDoNotChange(
                () -> resurrectPlayer.perform(alivePlayer.getId()),
                () -> playerRepository.find(alivePlayer.getId()).get().getExperience(),
                () -> playerRepository.find(alivePlayer.getId()).get().getHealth()
        );
    }

    @Test
    public void whenPlayerIsDead_clearsExperience() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(playerRepository.find(deadPlayer.getId()).get().getExperience(), equalTo(0));

        resurrectPlayer.perform(veryDeadPlayer.getId());
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().getExperience(), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_restoresHealth() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(playerRepository.find(deadPlayer.getId()).get().getHealth(), equalTo(GameConstants.STARTING_HEALTH));

        resurrectPlayer.perform(veryDeadPlayer.getId());
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().getHealth(), equalTo(GameConstants.STARTING_HEALTH));
    }

    @Test
    public void whenPlayerIsDead_sendsNotification() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId());
        assertThat(notifier.notificationsSent().get(0).getPlayerId(), equalTo(deadPlayer.getId()));
        assertThat(notifier.notificationsSent().get(0).getType(), equalTo(Notification.NotificationType.PLAYER_HAS_DIED));
    }

    @Test
    public void whenPlayerDoesNotExist_throwsException() {
        try{
            resurrectPlayer.perform(new PlayerId("no-such-id"));
            fail("Expected a NoSuchPlayerException to be thrown");
        } catch(NoSuchPlayerException e) {
            assertThat(e.getPlayerId(), equalTo(new PlayerId("no-such-id")));
        }
    }
}
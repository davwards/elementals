package com.davwards.elementals.players;

import com.davwards.elementals.players.PlayerGameConstants;
import com.davwards.elementals.players.notification.Notification;
import com.davwards.elementals.players.notification.FakeNotifier;
import com.davwards.elementals.players.ResurrectPlayer;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.persistence.PlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.support.test.Assertions.assertThatValues;
import static com.davwards.elementals.support.test.Factories.randomString;
import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ResurrectPlayerTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private FakeNotifier notifier = new FakeNotifier();
    private ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, notifier);

    private SavedPlayer deadPlayer = playerRepository.save(randomUnsavedPlayer()
            .withName("dead")
            .withExperience(100)
            .withCoin(200)
            .withHealth(0));

    private SavedPlayer veryDeadPlayer = playerRepository.save(randomUnsavedPlayer()
            .withName("very dead")
            .withExperience(100)
            .withCoin(200)
            .withHealth(-10));

    private SavedPlayer alivePlayer = playerRepository.save(randomUnsavedPlayer()
            .withName("alive")
            .withExperience(100)
            .withCoin(200)
            .withHealth(10));

    @Test
    public void whenPlayerIsAlive_doesNothing() throws Exception {
        assertThatValues(
                () -> playerRepository.find(alivePlayer.getId()).get().experience(),
                () -> playerRepository.find(alivePlayer.getId()).get().health(),
                () -> playerRepository.find(alivePlayer.getId()).get().coin()
        ).doNotChangeWhen(
                () -> resurrectPlayer.perform(alivePlayer.getId(), noopOutcome)
        );
    }

    @Test
    public void whenPlayerIsAlive_returnsPlayerDidNotNeedToBeResurrectedOutcome() throws Exception {
        SavedPlayer result = resurrectPlayer.perform(
                alivePlayer.getId(),
                new ResurrectPlayer.Outcome<SavedPlayer>() {
                    @Override
                    public SavedPlayer noSuchPlayer() {
                        fail("Expected playerDidNotNeedToBeResurrected outcome");
                        return null;
                    }

                    @Override
                    public SavedPlayer playerWasResurrected(SavedPlayer updatedPlayer) {
                        fail("Expected playerDidNotNeedToBeResurrected outcome");
                        return null;
                    }

                    @Override
                    public SavedPlayer playerDidNotNeedToBeResurrected(SavedPlayer player) {
                        return player;
                    }
                }
        );

        assertThat(result, equalTo(alivePlayer));
    }

    @Test
    public void whenPlayerIsDead_clearsExperience() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(deadPlayer.getId()).get().experience(), equalTo(0));

        resurrectPlayer.perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().experience(), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_restoresHealth() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(deadPlayer.getId()).get().health(), equalTo(PlayerGameConstants.STARTING_HEALTH));

        resurrectPlayer.perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().health(), equalTo(PlayerGameConstants.STARTING_HEALTH));
    }

    @Test
    public void whenPlayerIsDead_clearsCoin() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(deadPlayer.getId()).get().coin(), equalTo(0));

        resurrectPlayer.perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().coin(), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_sendsNotification() throws Exception {
        resurrectPlayer.perform(deadPlayer.getId(), noopOutcome);
        assertThat(notifier.notificationsSent().get(0).getPlayerId(), equalTo(deadPlayer.getId()));
        assertThat(notifier.notificationsSent().get(0).getType(), equalTo(Notification.NotificationType.PLAYER_HAS_DIED));
    }

    @Test
    public void whenPlayerIsDead_returnsPlayerWasResurrectedOutcome() throws Exception {
        SavedPlayer result = resurrectPlayer.perform(
                deadPlayer.getId(),
                new ResurrectPlayer.Outcome<SavedPlayer>() {
                    @Override
                    public SavedPlayer noSuchPlayer() {
                        fail("Expected playerWasResurrected outcome");
                        return null;
                    }

                    @Override
                    public SavedPlayer playerWasResurrected(SavedPlayer updatedPlayer) {
                        return updatedPlayer;
                    }

                    @Override
                    public SavedPlayer playerDidNotNeedToBeResurrected(SavedPlayer player) {
                        fail("Expected playerWasResurrected outcome");
                        return null;
                    }
                }
        );
        assertThat(result, equalTo(playerRepository.find(deadPlayer.getId()).get()));
    }

    @Test
    public void whenPlayerDoesNotExist_returnsNoSuchPlayerOutcome() {
        String expectedResult = randomString(10);

        String result = resurrectPlayer.perform(
                new PlayerId("no-such-id"),
                new ResurrectPlayer.Outcome<String>() {
                    @Override
                    public String noSuchPlayer() {
                        return expectedResult;
                    }

                    @Override
                    public String playerWasResurrected(SavedPlayer updatedPlayer) {
                        fail("Expected noSuchPlayer outcome");
                        return null;
                    }

                    @Override
                    public String playerDidNotNeedToBeResurrected(SavedPlayer player) {
                        fail("Expected noSuchPlayer outcome");
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }

    private final ResurrectPlayer.Outcome<Void> noopOutcome = new ResurrectPlayer.Outcome<Void>() {
        @Override
        public Void noSuchPlayer() {
            return null;
        }

        @Override
        public Void playerWasResurrected(SavedPlayer updatedPlayer) {
            return null;
        }

        @Override
        public Void playerDidNotNeedToBeResurrected(SavedPlayer player) {
            return null;
        }
    };
}
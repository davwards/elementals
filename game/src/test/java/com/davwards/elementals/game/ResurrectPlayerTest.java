package com.davwards.elementals.game;

import com.davwards.elementals.game.notification.fakes.FakeNotifier;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.notification.Notification;
import com.davwards.elementals.game.players.*;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatValues;
import static com.davwards.elementals.TestUtils.randomString;
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
        assertThat(playerRepository.find(deadPlayer.getId()).get().health(), equalTo(GameConstants.STARTING_HEALTH));

        resurrectPlayer.perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerRepository.find(veryDeadPlayer.getId()).get().health(), equalTo(GameConstants.STARTING_HEALTH));
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
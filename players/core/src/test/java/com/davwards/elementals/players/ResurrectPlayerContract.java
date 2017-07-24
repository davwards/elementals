package com.davwards.elementals.players;

import com.davwards.elementals.players.notification.Notification;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.models.UnsavedPlayer;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

import static com.davwards.elementals.support.test.Assertions.assertThatValues;
import static com.davwards.elementals.support.test.Factories.randomString;
import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class ResurrectPlayerContract {

    abstract ResurrectPlayer useCase();

    abstract FetchPlayer fetchPlayer();

    abstract SavedPlayer givenAnExistingPlayer(UnsavedPlayer player);

    abstract void assertThatNotificationWasSent(PlayerId playerId, Notification.NotificationType type);

    private SavedPlayer deadPlayer;
    private SavedPlayer veryDeadPlayer;
    private SavedPlayer alivePlayer;

    @Before
    public void seedData() {
        alivePlayer = givenAnExistingPlayer(randomUnsavedPlayer()
                .withName("alive")
                .withExperience(100)
                .withCoin(200)
                .withHealth(10));

        deadPlayer = givenAnExistingPlayer(randomUnsavedPlayer()
                .withName("dead")
                .withExperience(100)
                .withCoin(200)
                .withHealth(0));

        veryDeadPlayer = givenAnExistingPlayer(randomUnsavedPlayer()
                .withName("very dead")
                .withExperience(100)
                .withCoin(200)
                .withHealth(-10));
    }

    @Test
    public void whenPlayerIsAlive_doesNothing() throws Exception {
        assertThatValues(
                () -> playerExperience(alivePlayer.getId()),
                () -> playerHealth(alivePlayer.getId()),
                () -> playerCoin(alivePlayer.getId())
        ).doNotChangeWhen(
                () -> useCase().perform(alivePlayer.getId(), noopOutcome)
        );
    }

    @Test
    public void whenPlayerIsAlive_returnsPlayerDidNotNeedToBeResurrectedOutcome() throws Exception {
        SavedPlayer result = useCase().perform(
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
        useCase().perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerExperience(deadPlayer.getId()), equalTo(0));

        useCase().perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerExperience(veryDeadPlayer.getId()), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_restoresHealth() throws Exception {
        useCase().perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerHealth(deadPlayer.getId()), equalTo(PlayerGameConstants.STARTING_HEALTH));

        useCase().perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerHealth(veryDeadPlayer.getId()), equalTo(PlayerGameConstants.STARTING_HEALTH));
    }

    @Test
    public void whenPlayerIsDead_clearsCoin() throws Exception {
        useCase().perform(deadPlayer.getId(), noopOutcome);
        assertThat(playerCoin(deadPlayer.getId()), equalTo(0));

        useCase().perform(veryDeadPlayer.getId(), noopOutcome);
        assertThat(playerCoin(veryDeadPlayer.getId()), equalTo(0));
    }

    @Test
    public void whenPlayerIsDead_sendsNotification() throws Exception {
        useCase().perform(deadPlayer.getId(), noopOutcome);
        assertThatNotificationWasSent(deadPlayer.getId(), Notification.NotificationType.PLAYER_HAS_DIED);
    }

    @Test
    public void whenPlayerIsDead_returnsPlayerWasResurrectedOutcome() throws Exception {
        SavedPlayer result = useCase().perform(
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
        assertThat(result, equalTo(playerAttribute(deadPlayer.getId(), player -> player)));
    }

    @Test
    public void whenPlayerDoesNotExist_returnsNoSuchPlayerOutcome() {
        String expectedResult = randomString(10);

        String result = useCase().perform(
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

    private Integer playerHealth(PlayerId id) {
        return playerAttribute(id, SavedPlayer::health);
    }

    private Integer playerExperience(PlayerId id) {
        return playerAttribute(id, SavedPlayer::experience);
    }

    private Integer playerCoin(PlayerId id) {
        return playerAttribute(id, SavedPlayer::coin);
    }

    private <T> T playerAttribute(PlayerId id, Function<SavedPlayer, T> getter) {
        return fetchPlayer().perform(id, new FetchPlayer.Outcome<T>() {
            @Override
            public T foundPlayer(SavedPlayer player) {
                return getter.apply(player);
            }

            @Override
            public T noSuchPlayer() {
                fail("Expected to find a player with id " + id);
                return null;
            }
        });
    }
}
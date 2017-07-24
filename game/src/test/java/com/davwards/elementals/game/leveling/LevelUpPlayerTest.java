package com.davwards.elementals.game.leveling;

import com.davwards.elementals.players.models.Player;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.support.test.Factories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class LevelUpPlayerTest {

    @Test
    public void whenPlayerDoesNotExist_returnsNoSuchPlayerOutcome() {
        String noise = randomString(10);
        String result = new LevelUpPlayer(playerRepository, new DummyLevelCheck())
                .perform(
                        new PlayerId("no-such-player"), expectNoSuchPlayerOutcome(noise)
                );

        assertThat(result, equalTo(noise));
    }

    @Test
    public void whenPlayerCanLevelUp_deductsExperienceCost() {
        Integer costToLevel = randomInt(20, 70);
        SavedPlayer player = playerRepository.save(
                randomUnsavedPlayer().withExperience(costToLevel + 10)
        );

        SavedPlayer updatedPlayer = new LevelUpPlayer(
                playerRepository,
                new StubPositiveLevelCheck(costToLevel)
        ).perform(
                player.getId(),
                expectSuccessfullyLeveledUpOutcome
        );

        assertThat(updatedPlayer.experience(), equalTo(10));
    }

    @Test
    public void whenPlayerCanLevelUp_increasesLevelByOne() {
        SavedPlayer player = playerRepository.save(
                randomUnsavedPlayer().withLevel(4).withExperience(10)
        );

        SavedPlayer updatedPlayer = new LevelUpPlayer(
                playerRepository,
                new StubPositiveLevelCheck(1)
        ).perform(
                player.getId(),
                expectSuccessfullyLeveledUpOutcome
        );

        assertThat(updatedPlayer.level(), equalTo(5));
    }

    @Test
    public void whenPlayerCanLevelUp_updatesPlayer() {
        Integer costToLevel = randomInt(20, 70);
        SavedPlayer player = playerRepository.save(
                randomUnsavedPlayer().withExperience(costToLevel + 10)
        );

        SavedPlayer updatedPlayer = new LevelUpPlayer(
                playerRepository,
                new StubPositiveLevelCheck(costToLevel)
        ).perform(
                player.getId(),
                expectSuccessfullyLeveledUpOutcome
        );

        assertThat(updatedPlayer, equalTo(playerRepository.find(updatedPlayer.getId()).get()));
    }

    @Test
    public void whenPlayerCannotLevelUp_returnsCannotLevelUpOutcome() throws Exception {
        SavedPlayer player = playerRepository.save(randomUnsavedPlayer());
        String noise = randomString(10);
        String result = new LevelUpPlayer(
                playerRepository,
                new StubNegativeLevelCheck()
        ).perform(player.getId(), expectPlayerCannotLevelUpOutcome(noise));

        assertThat(result, equalTo(noise));
    }

    private LevelUpPlayer.Outcome<String> expectPlayerCannotLevelUpOutcome(String noise) {
        return new LevelUpPlayer.Outcome<String>() {
            @Override
            public String successfullyUpdatedPlayer(SavedPlayer updatedPlayer) {
                fail("Expected playerCannotLevel outcome");
                return null;
            }

            @Override
            public String playerCannotLevel() {
                return noise;
            }

            @Override
            public String noSuchPlayer() {
                fail("Expected playerCannotLevel outcome");
                return null;
            }
        };
    }

    private class StubPositiveLevelCheck implements CheckWhetherPlayerCanLevelUp {

        private final Integer cost;

        private StubPositiveLevelCheck(Integer cost) {
            this.cost = cost;
        }

        @Override
        public <T> T perform(Player player, Outcome<T> outcome) {
            return outcome.playerCanLevelUp(cost);
        }

    }

    private final InMemoryPlayerRepository playerRepository = new InMemoryPlayerRepository();

    private final LevelUpPlayer.Outcome<SavedPlayer> expectSuccessfullyLeveledUpOutcome = new LevelUpPlayer.Outcome<SavedPlayer>() {
        @Override
        public SavedPlayer successfullyUpdatedPlayer(SavedPlayer updatedPlayer) {
            return updatedPlayer;
        }

        @Override
        public SavedPlayer playerCannotLevel() {
            fail("Expected successfullyUpdatedPlayer outcome");
            return null;
        }

        @Override
        public SavedPlayer noSuchPlayer() {
            fail("Expected successfullyUpdatedPlayer outcome");
            return null;
        }
    };

    private LevelUpPlayer.Outcome<String> expectNoSuchPlayerOutcome(String noise) {
        return new LevelUpPlayer.Outcome<String>() {
            @Override
            public String successfullyUpdatedPlayer(SavedPlayer updatedPlayer) {
                fail("Expected noSuchPlayer outcome");
                return null;
            }

            @Override
            public String playerCannotLevel() {
                fail("Expected noSuchPlayer outcome");
                return null;
            }

            @Override
            public String noSuchPlayer() {
                return noise;
            }
        };
    }

    private class DummyLevelCheck implements CheckWhetherPlayerCanLevelUp {
        @Override
        public <T> T perform(Player player, Outcome<T> outcome) {
            fail("dummy should not have been invoked");
            return null;
        }
    }

    private class StubNegativeLevelCheck implements CheckWhetherPlayerCanLevelUp {
        @Override
        public <T> T perform(Player player, Outcome<T> outcome) {
            return outcome.playerCannotLevelUp(100);
        }
    }
}
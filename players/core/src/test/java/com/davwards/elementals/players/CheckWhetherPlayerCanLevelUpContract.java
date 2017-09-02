package com.davwards.elementals.players;

import com.davwards.elementals.players.models.ImmutableUnsavedPlayer;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public abstract class CheckWhetherPlayerCanLevelUpContract {
    abstract CheckWhetherPlayerCanLevelUp useCase();

    @Test
    public void scenarios() throws Exception {
        forAVarietyOfPlayersWithDifferentLevelsAndExperience(player ->
                useCase().perform(player, new CheckWhetherPlayerCanLevelUp.Outcome<Runnable>() {
                    @Override
                    public Runnable playerCanLevelUp(Integer experienceCost) {
                        return () -> checkInvariantsForPlayerThatCanLevel(player, experienceCost);
                    }

                    @Override
                    public Runnable playerCannotLevelUp(Integer additionalCost) {
                        return () -> checkInvariantsForPlayerThatCannotLevel(player, additionalCost);
                    }
                }).run()
        );
    }

    private void forAVarietyOfPlayersWithDifferentLevelsAndExperience(Consumer<ImmutableUnsavedPlayer> checkInvariants) {
        Stream.of(
                randomUnsavedPlayer().withLevel(1).withExperience(0),
                randomUnsavedPlayer().withLevel(1).withExperience(10),
                randomUnsavedPlayer().withLevel(1).withExperience(500),
                randomUnsavedPlayer().withLevel(2).withExperience(0),
                randomUnsavedPlayer().withLevel(2).withExperience(20),
                randomUnsavedPlayer().withLevel(2).withExperience(200),
                randomUnsavedPlayer().withLevel(13).withExperience(2),
                randomUnsavedPlayer().withLevel(13).withExperience(10000),
                randomUnsavedPlayer().withLevel(100).withExperience(1),
                randomUnsavedPlayer().withLevel(100).withExperience(10000)
        ).forEach(checkInvariants);
    }

    private void checkInvariantsForPlayerThatCanLevel(ImmutableUnsavedPlayer player, Integer cost) {
        assertThat(
                "Since " + player + " can level, cost to level should not be greater than player's experience",
                cost,
                not(greaterThan(player.experience()))
        );

        ImmutableUnsavedPlayer playerWithSlightlyMoreExperience =
                player.withExperience(player.experience() + 1);

        ImmutableUnsavedPlayer playerWithMuchMoreExperience =
                player.withExperience(player.experience() + 1000);

        useCase().perform(playerWithSlightlyMoreExperience, new CheckWhetherPlayerCanLevelUp.Outcome<Void>() {
            @Override
            public Void playerCanLevelUp(Integer experienceCost) {
                // pass
                return null;
            }

            @Override
            public Void playerCannotLevelUp(Integer additionalCost) {
                fail("Since player " + player + " can level, same-level player with slightly more experience should also be able to level");
                return null;
            }
        });

        useCase().perform(playerWithMuchMoreExperience, new CheckWhetherPlayerCanLevelUp.Outcome<Void>() {
            @Override
            public Void playerCanLevelUp(Integer experienceCost) {
                // pass
                return null;
            }

            @Override
            public Void playerCannotLevelUp(Integer additionalCost) {
                fail("Since player " + player + " can level, same-level player with much more experience should also be able to level");
                return null;
            }
        });
    }

    private void checkInvariantsForPlayerThatCannotLevel(ImmutableUnsavedPlayer player, Integer deficit) {
        ImmutableUnsavedPlayer playerWithMoreExperience =
                player.withExperience(player.experience() + deficit);

        useCase().perform(playerWithMoreExperience, new CheckWhetherPlayerCanLevelUp.Outcome<Void>() {
            @Override
            public Void playerCanLevelUp(Integer experienceCost) {
                assertThat("Since " + player + " was " + deficit + " short of leveling, " + playerWithMoreExperience + " should have just enough experience to level",
                        experienceCost, is(playerWithMoreExperience.experience()));
                return null;
            }

            @Override
            public Void playerCannotLevelUp(Integer additionalCost) {
                fail("Since " + player + " was " + deficit + " short of leveling, " + playerWithMoreExperience + " should be able to level");
                return null;
            }
        });
    }
}

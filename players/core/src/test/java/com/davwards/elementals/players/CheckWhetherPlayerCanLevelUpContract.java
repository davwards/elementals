package com.davwards.elementals.players;

import com.davwards.elementals.players.CheckWhetherPlayerCanLevelUp;
import com.davwards.elementals.players.models.Player;
import org.junit.Test;

import java.util.stream.Stream;

import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public abstract class CheckWhetherPlayerCanLevelUpContract {
    abstract CheckWhetherPlayerCanLevelUp useCase();

    @Test
    public void scenarios() throws Exception {
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
        ).forEach(player -> {

            Result result = performUseCase(player);

            if (result.canLevel) {

                assertThat(
                        "Cost to level is not greater than player's experience",
                        result.costOrDeficit,
                        not(greaterThan(result.player.experience()))
                );

                assertThat(
                        "Same-level player with more experience would also be able to level",
                        performUseCase(
                                player.withExperience(player.experience() + 1)
                        ).canLevel,
                        is(true)
                );

                assertThat(
                        "Same-level player with much more experience would also be able to level",
                        performUseCase(
                                player.withExperience(player.experience() + 1000)
                        ).canLevel,
                        is(true)
                );

            } else {

                Result newResult = performUseCase(
                        player.withExperience(player.experience() + result.costOrDeficit)
                );

                assertThat("Player " + newResult.player + " can level",
                        newResult.canLevel, is(true));
                assertThat("Player " + newResult.player + " would have just enough experience to level",
                        newResult.costOrDeficit, is(newResult.player.experience()));
            }
        });

    }

    private Result performUseCase(final Player player) {
        return useCase().perform(player, new CheckWhetherPlayerCanLevelUp.Outcome<Result>() {
            @Override
            public Result playerCanLevelUp(Integer experienceCost) {
                return new Result(player, true, experienceCost);
            }

            @Override
            public Result playerCannotLevelUp(Integer additionalCost) {
                return new Result(player, false, additionalCost);
            }
        });
    }

    private static class Result {
        final Player player;
        final Boolean canLevel;
        final Integer costOrDeficit;

        private Result(Player player, Boolean canLevel, Integer costOrDeficit) {
            this.player = player;
            this.canLevel = canLevel;
            this.costOrDeficit = costOrDeficit;
        }
    }
}
package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.KindOfLootId;
import com.davwards.elementals.game.players.models.Player;
import org.junit.Test;

import static com.davwards.elementals.game.support.test.Factories.randomInt;
import static com.davwards.elementals.game.support.test.Factories.randomString;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class CheckWhetherPlayerCanPurchaseLootContract {
    abstract CheckWhetherPlayerCanPurchaseLoot useCase();

    protected interface TestCase {
        void runTestWith(KindOfLootId kindId, Integer cost, Integer requiredLevel);
    }

    abstract void givenAnExpensiveHighLevelKindOfLoot(TestCase testCase);

    @Test
    public void whenPlayerHasEnoughCoinAndLevels_returnsSuccessOutcomeWithCost() {
        String noise = randomString(10);

        givenAnExpensiveHighLevelKindOfLoot((kindId, cost, requiredLevel) -> {
            Player playerWithEnoughCoinAndLevels = randomUnsavedPlayer()
                    .withLevel(requiredLevel)
                    .withCoin(cost);

            assertThat(
                    performUseCase(playerWithEnoughCoinAndLevels, kindId, noise),
                    equalTo("player can purchase for " + cost + " - " + noise)
            );
        });
    }

    @Test
    public void whenPlayerHasNotEnoughCoin_returnsNotEnoughCoinWithDeficit() {
        String noise = randomString(10);
        Integer deficit = randomInt(1,4);

        givenAnExpensiveHighLevelKindOfLoot((kindId, cost, requiredLevel) -> {
            Player playerWithNotEnoughCoin = randomUnsavedPlayer()
                    .withLevel(requiredLevel)
                    .withCoin(cost - deficit);

            assertThat(
                    performUseCase(playerWithNotEnoughCoin, kindId, noise),
                    equalTo("player needs " + deficit + " more coin - " + noise)
            );
        });
    }

    @Test
    public void whenPlayerHasNotEnoughLevels_returnsNotEnoughLevelsWithDeficit() {
        String noise = randomString(10);
        Integer deficit = randomInt(1,4);
        givenAnExpensiveHighLevelKindOfLoot((kindId, cost, requiredLevel) -> {
            Player playerWithNotEnoughLevels = randomUnsavedPlayer()
                    .withLevel(requiredLevel - deficit)
                    .withCoin(cost);

            assertThat(
                    performUseCase(playerWithNotEnoughLevels, kindId, noise),
                    equalTo("player needs " + deficit + " more levels - " + noise)
            );
        });
    }

    @Test
    public void whenPlayerHasNotEnoughCoinNorLevels_returnsNotEnoughLevelsWithDeficit() {
        String noise = randomString(10);
        Integer deficit = randomInt(1,4);
        givenAnExpensiveHighLevelKindOfLoot((kindId, cost, requiredLevel) -> {
            Player playerWithNotEnoughCoinNorLevels = randomUnsavedPlayer()
                    .withLevel(requiredLevel - deficit)
                    .withCoin(cost - deficit);

            assertThat(
                    performUseCase(playerWithNotEnoughCoinNorLevels, kindId, noise),
                    equalTo("player needs " + deficit + " more levels - " + noise)
            );
        });
    }

    private String performUseCase(Player player, KindOfLootId kindId, String noise) {
        return useCase().perform(
                player,
                kindId,
                new CheckWhetherPlayerCanPurchaseLoot.Outcome<String>() {
                    @Override
                    public String playerCanPurchaseLoot(Integer cost) {
                        return "player can purchase for " + cost + " - " + noise;
                    }

                    @Override
                    public String playerDoesNotHaveEnoughCoin(Integer coinDeficit) {
                        return "player needs " + coinDeficit + " more coin - " + noise;
                    }

                    @Override
                    public String playerIsNotHighEnoughLevel(Integer levelDeficit) {
                        return "player needs " + levelDeficit + " more levels - " + noise;
                    }
                }
        );
    }
}
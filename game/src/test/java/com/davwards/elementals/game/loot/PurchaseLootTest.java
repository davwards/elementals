package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.persistence.InMemoryLootRepository;
import com.davwards.elementals.game.loot.persistence.LootRepository;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.game.loot.models.KindOfLootId.COPPER_SWORD;
import static com.davwards.elementals.game.support.test.Factories.randomString;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedPlayer;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class PurchaseLootTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private SavedPlayer player = playerRepository.save(randomUnsavedPlayer().withCoin(200));
    private LootCostCalculator stubCalculator = kind -> 199;
    private LootRepository lootRepository = new InMemoryLootRepository();
    private PurchaseLoot purchaseLoot = new PurchaseLoot(lootRepository, playerRepository, stubCalculator);

    @Test
    public void whenPlayerHasEnoughCoin_subtractsCostFromPlayerCoin() {
        purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                new PurchaseLoot.Outcome<Void>() {
                    @Override
                    public Void successfullyPurchasedLoot(SavedLoot savedLoot) {
                        return null;
                    }

                    @Override
                    public Void noSuchPlayer() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }

                    @Override
                    public Void notEnoughCoin() {
                        return null;
                    }
                }
        );

        assertThat(playerRepository.find(player.getId()).get().coin(), equalTo(200-199));
    }

    @Test
    public void whenPlayerHasEnoughCoin_returnsSuccessOutcome() {
        SavedLoot loot = purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                new PurchaseLoot.Outcome<SavedLoot>() {
                    @Override
                    public SavedLoot successfullyPurchasedLoot(SavedLoot savedLoot) {
                        return savedLoot;
                    }

                    @Override
                    public SavedLoot noSuchPlayer() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }

                    @Override
                    public SavedLoot notEnoughCoin() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }
                }
        );

        assertThat(loot, equalTo(lootRepository.find(loot.getId()).get()));
    }

    @Test
    public void whenPlayerDoesNotExist() {
        String expectedResult = randomString(10);

        String result = purchaseLoot.perform(
                new PlayerId("nonsense"),
                COPPER_SWORD,
                new PurchaseLoot.Outcome<String>() {
                    @Override
                    public String successfullyPurchasedLoot(SavedLoot savedLoot) {
                        fail("Expected noSuchPlayer outcome");
                        return null;
                    }

                    @Override
                    public String noSuchPlayer() {
                        return expectedResult;
                    }

                    @Override
                    public String notEnoughCoin() {
                        fail("Expected noSuchPlayer outcome");
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void whenPlayerDoesNotHaveEnoughCoin_returnsNotEnoughCoinOutcome() {
        purchaseLoot = new PurchaseLoot(
                lootRepository,
                playerRepository,
                kind -> player.coin() + 1
        );

        String expectedResult = randomString(10);

        String result = purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                new PurchaseLoot.Outcome<String>() {
                    @Override
                    public String successfullyPurchasedLoot(SavedLoot savedLoot) {
                        fail("Expected notEnoughCoin outcome");
                        return null;
                    }

                    @Override
                    public String noSuchPlayer() {
                        fail("Expected notEnoughCoin outcome");
                        return null;
                    }

                    @Override
                    public String notEnoughCoin() {
                        return expectedResult;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void whenPlayerHasJustEnoughCoin_returnsSuccess() {
        purchaseLoot = new PurchaseLoot(
                lootRepository,
                playerRepository,
                kind -> player.coin()
        );

        String expectedResult = randomString(10);

        String result = purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                new PurchaseLoot.Outcome<String>() {
                    @Override
                    public String successfullyPurchasedLoot(SavedLoot savedLoot) {
                        return expectedResult;
                    }

                    @Override
                    public String noSuchPlayer() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }

                    @Override
                    public String notEnoughCoin() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }
}
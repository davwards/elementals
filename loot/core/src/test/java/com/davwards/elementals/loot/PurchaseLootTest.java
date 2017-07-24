package com.davwards.elementals.loot;

import com.davwards.elementals.loot.models.KindOfLootId;
import com.davwards.elementals.loot.models.SavedLoot;
import com.davwards.elementals.loot.persistence.InMemoryLootRepository;
import com.davwards.elementals.loot.persistence.LootRepository;
import com.davwards.elementals.players.models.Player;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.persistence.PlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.loot.models.KindOfLootId.COPPER_SWORD;
import static com.davwards.elementals.support.test.Factories.randomString;
import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class PurchaseLootTest {

    private static final Integer LOOT_COST = 20;
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private SavedPlayer player = playerRepository.save(randomUnsavedPlayer().withCoin(200));
    private LootRepository lootRepository = new InMemoryLootRepository();
    private PurchaseLoot purchaseLoot = new PurchaseLoot(
            lootRepository,
            playerRepository,
            new DummyLootCheck()
    );

    @Test
    public void whenPlayerCanPurchaseLoot_subtractsCostFromPlayerCoin() {
        givenLootCheckIs(new SuccessfulLootCheck());
        purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                outcome("")
        );

        assertThat(playerRepository.find(player.getId()).get().coin(), equalTo(200-LOOT_COST));
    }

    @Test
    public void whenPlayerCanPurchaseLoot_returnsSuccessOutcomeWithCreatedLoot() {
        givenLootCheckIs(new SuccessfulLootCheck());
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

                    @Override
                    public SavedLoot notHighEnoughLevel() {
                        fail("Expected successfullyPurchasedLoot outcome");
                        return null;
                    }
                }
        );

        assertThat(loot, equalTo(lootRepository.find(loot.getId()).get()));
    }

    @Test
    public void whenPlayerDoesNotHaveEnoughCoin_returnsNotEnoughCoinOutcome() {
        givenLootCheckIs(new NotEnoughCoinLootCheck());

        String noise = randomString(10);

        String result = purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                outcome(noise)
        );

        assertThat(result, equalTo("not enough coin - " + noise));
    }

    @Test
    public void whenPlayerIsNotHighEnoughLevel_returnsNotEnoughCoinOutcome() {
        givenLootCheckIs(new NotHighEnoughLevelLootCheck());

        String noise = randomString(10);
        String result = purchaseLoot.perform(
                player.getId(),
                COPPER_SWORD,
                outcome(noise)
        );

        assertThat(result, equalTo("not high enough level - " + noise));
    }

    @Test
    public void whenPlayerDoesNotExist() {
        String noise = randomString(10);

        String result = purchaseLoot.perform(
                new PlayerId("nonsense"),
                COPPER_SWORD,
                outcome(noise)
        );

        assertThat(result, equalTo("no such player - " + noise));
    }

    private PurchaseLoot.Outcome<String> outcome(String noise) {
        return new PurchaseLoot.Outcome<String>() {
            @Override
            public String successfullyPurchasedLoot(SavedLoot savedLoot) {
                return "purchased " + savedLoot + " - " + noise;
            }

            @Override
            public String noSuchPlayer() {
                return "no such player - " + noise;
            }

            @Override
            public String notEnoughCoin() {
                return "not enough coin - " + noise;
            }

            @Override
            public String notHighEnoughLevel() {
                return "not high enough level - " + noise;
            }
        };
    }

    private void givenLootCheckIs(CheckWhetherPlayerCanPurchaseLoot lootCheck) {
        purchaseLoot = new PurchaseLoot(lootRepository, playerRepository, lootCheck);
    }

    private class DummyLootCheck implements CheckWhetherPlayerCanPurchaseLoot {
        @Override
        public <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome) {
            throw new RuntimeException("Dummy should not have been invoked");
        }
    }

    private class SuccessfulLootCheck implements CheckWhetherPlayerCanPurchaseLoot {
        @Override
        public <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome) {
            return outcome.playerCanPurchaseLoot(LOOT_COST);
        }
    }

    private class NotEnoughCoinLootCheck implements CheckWhetherPlayerCanPurchaseLoot {
        @Override
        public <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome) {
            return outcome.playerDoesNotHaveEnoughCoin(10);
        }
    }

    private class NotHighEnoughLevelLootCheck implements CheckWhetherPlayerCanPurchaseLoot {
        @Override
        public <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome) {
            return outcome.playerIsNotHighEnoughLevel(3);
        }
    }
}
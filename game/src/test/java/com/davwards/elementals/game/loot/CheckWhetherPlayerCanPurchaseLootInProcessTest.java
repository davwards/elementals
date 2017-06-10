package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.KindOfLootId;

public class CheckWhetherPlayerCanPurchaseLootInProcessTest extends CheckWhetherPlayerCanPurchaseLootContract {
    private CheckWhetherPlayerCanPurchaseLoot useCase =
            new CheckWhetherPlayerCanPurchaseLoot.InProcess();

    @Override
    CheckWhetherPlayerCanPurchaseLoot useCase() {
        return useCase;
    }

    @Override
    void givenAnExpensiveHighLevelKindOfLoot(TestCase testCase) {
        testCase.runTestWith(KindOfLootId.VORPAL_SWORD, 1000, 80);
    }
}

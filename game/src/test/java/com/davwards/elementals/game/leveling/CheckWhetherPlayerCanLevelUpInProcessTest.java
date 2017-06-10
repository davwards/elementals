package com.davwards.elementals.game.leveling;

public class CheckWhetherPlayerCanLevelUpInProcessTest extends CheckWhetherPlayerCanLevelUpContract {
    CheckWhetherPlayerCanLevelUp objectUnderTest = new CheckWhetherPlayerCanLevelUpInProcess();

    @Override
    CheckWhetherPlayerCanLevelUp useCase() {
        return objectUnderTest;
    }
}

package com.davwards.elementals.game.leveling;

public class CheckWhetherPlayerCanLevelUpInProcessTest extends CheckWhetherPlayerCanLevelUpContract {
    CheckWhetherPlayerCanLevelUp objectUnderTest = new CheckWhetherPlayerCanLevelUp.InProcess();

    @Override
    CheckWhetherPlayerCanLevelUp useCase() {
        return objectUnderTest;
    }
}

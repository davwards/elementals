package com.davwards.elementals.players;

public class CheckWhetherPlayerCanLevelUpInProcessTest extends CheckWhetherPlayerCanLevelUpContract {
    CheckWhetherPlayerCanLevelUp objectUnderTest = new CheckWhetherPlayerCanLevelUp.InProcess();

    @Override
    CheckWhetherPlayerCanLevelUp useCase() {
        return objectUnderTest;
    }
}

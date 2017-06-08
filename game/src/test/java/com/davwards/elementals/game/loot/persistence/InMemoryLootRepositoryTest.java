package com.davwards.elementals.game.loot.persistence;

public class InMemoryLootRepositoryTest extends LootRepositoryTest {
    private LootRepository repository = new InMemoryLootRepository();

    @Override
    protected LootRepository repository() {
        return repository;
    }
}

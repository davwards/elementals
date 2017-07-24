package com.davwards.elementals.loot.persistence;

public class InMemoryLootRepositoryTest extends LootRepositoryTest {
    private LootRepository repository = new InMemoryLootRepository();

    @Override
    protected LootRepository repository() {
        return repository;
    }
}

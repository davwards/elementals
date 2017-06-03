package com.davwards.elementals.game.tasks.persistence;

public class InMemoryRecurringTaskRepositoryTest extends RecurringTaskRepositoryTest {
    InMemoryRecurringTaskRepository repository = new InMemoryRecurringTaskRepository();

    @Override
    protected RecurringTaskRepository repository() {
        return repository;
    }
}

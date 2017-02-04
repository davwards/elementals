package com.davwards.elementals.game.entities.tasks.recurring;

public class InMemoryRecurringTaskRepositoryTest extends RecurringTaskRepositoryTest {
    private InMemoryRecurringTaskRepository repository = new InMemoryRecurringTaskRepository();

    @Override
    protected RecurringTaskRepository repository() {
        return repository;
    }
}

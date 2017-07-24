package com.davwards.elementals.tasks.persistence;

public class InMemoryRecurringTaskRepositoryTest extends RecurringTaskRepositoryTest {
    InMemoryRecurringTaskRepository repository = new InMemoryRecurringTaskRepository();

    @Override
    protected RecurringTaskRepository repository() {
        return repository;
    }
}

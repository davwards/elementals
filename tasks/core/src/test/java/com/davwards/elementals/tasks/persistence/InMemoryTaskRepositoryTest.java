package com.davwards.elementals.tasks.persistence;

public class InMemoryTaskRepositoryTest extends TaskRepositoryTest {

    private InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();

    @Override
    protected TaskRepository repository() {
        return inMemoryTaskRepository;
    }
}
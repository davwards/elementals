package com.davwards.elementals.game.tasks.persistence;

public class InMemoryTaskRepositoryTest extends TaskRepositoryTest {

    private InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();

    @Override
    protected TaskRepository repository() {
        return inMemoryTaskRepository;
    }
}
package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.fakes.InMemoryTaskRepository;

public class InMemoryTaskRepositoryTest extends TaskRepositoryTest {

    private InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();

    @Override
    protected TaskRepository repository() {
        return inMemoryTaskRepository;
    }
}
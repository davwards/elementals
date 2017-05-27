package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;

public class InMemoryTaskRepositoryTest extends BetterTaskRepositoryTest {

    private InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();

    @Override
    protected TaskRepository repository() {
        return inMemoryTaskRepository;
    }
}
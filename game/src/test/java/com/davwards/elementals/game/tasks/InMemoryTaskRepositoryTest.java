package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

public class InMemoryTaskRepositoryTest extends TaskRepositoryTest {

    private InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();

    @Override
    protected TaskRepository repository() {
        return inMemoryTaskRepository;
    }
}
package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTodoRepository;

public class InMemoryTodoRepositoryTest extends TodoRepositoryTest {

    private InMemoryTodoRepository inMemoryTodoRepository = new InMemoryTodoRepository();

    @Override
    protected TodoRepository repository() {
        return inMemoryTodoRepository;
    }
}
package com.davwards.elementals.game.todos;

public class InMemoryTodoRepositoryTest extends TodoRepositoryTest {

    private InMemoryTodoRepository inMemoryTodoRepository = new InMemoryTodoRepository();

    @Override
    protected TodoRepository repository() {
        return inMemoryTodoRepository;
    }
}
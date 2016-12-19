package com.davwards.elementals.game.todos;

public class CompleteTodoUseCase {
    private final TodoRepository todoRepository;

    public CompleteTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public SavedTodo perform(TodoId id) throws NoSuchTodoException {
        return todoRepository.find(id)
                .map(todo -> {
                    todo.setStatus(Todo.Status.COMPLETE);
                    return todoRepository.save(todo);
                }).orElseThrow(() -> new NoSuchTodoException(id));
    }
}

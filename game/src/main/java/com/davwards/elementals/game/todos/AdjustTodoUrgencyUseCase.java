package com.davwards.elementals.game.todos;

import java.time.LocalDateTime;

public class AdjustTodoUrgencyUseCase {
    private final TodoRepository todoRepository;

    public AdjustTodoUrgencyUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void perform(TodoId id, LocalDateTime currentTime) throws NoSuchTodoException {
        todoRepository.find(id)
                .map(todo -> updateTodoUrgencyIfPastDue(todo, currentTime))
                .map(todoRepository::save)
                .orElseThrow(() -> new NoSuchTodoException(id));
    }

    private static <T extends Todo> T updateTodoUrgencyIfPastDue(T todo, LocalDateTime currentTime) {
        if(todoIsPastDue(todo, currentTime) && todo.isIncomplete()) {
            todo.setUrgency(Todo.Urgency.PAST_DUE);
        }

        return todo;
    }

    private static boolean todoIsPastDue(Todo todo, LocalDateTime currentTime) {
        return todo.getDeadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}

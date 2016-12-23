package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.todos.*;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;
import com.davwards.elementals.game.fakeplugins.InMemoryTodoRepository;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class FetchTodoTest {

    private TodoRepository todoRepository = new InMemoryTodoRepository();
    private FetchTodo fetchTodo = new FetchTodo(todoRepository);

    @Test
    public void whenTodoExists_returnsIt() throws Exception {
        SavedTodo todo =
                todoRepository.save(
                        new UnsavedTodo(new PlayerId("some-player"), "the title", Todo.Status.INCOMPLETE)
                );

        assertThat(fetchTodo.perform(todo.getId()), equalTo(todo));
    }

    @Test
    public void whenTodoDoesNotExist_throws() throws Exception {
        try {
            fetchTodo.perform(new TodoId("nonsense-id"));
            fail("Expected NoSuchTodoException to be thrown");
        } catch(NoSuchTodoException e) {
            assertThat(e.getTodoId(), equalTo(new TodoId("nonsense-id")));
        }
    }
}
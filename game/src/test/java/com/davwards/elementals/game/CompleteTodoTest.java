package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.todos.*;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;
import com.davwards.elementals.game.inmemorypersistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.inmemorypersistence.InMemoryTodoRepository;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatValueChanges;
import static com.davwards.elementals.TestUtils.assertThatValueIncreases;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteTodoTest {

    PlayerRepository playerRepository = new InMemoryPlayerRepository();
    TodoRepository todoRepository = new InMemoryTodoRepository();
    CompleteTodo completeTodo = new CompleteTodo(todoRepository, playerRepository);

    SavedPlayer existingPlayer = playerRepository.save(new UnsavedPlayer("test-player"));

    @Test
    public void whenTodoExists_marksTodoComplete() {
        SavedTodo todo = todoRepository.save(
                new UnsavedTodo(existingPlayer.getId(), "test todo", Todo.Status.INCOMPLETE)
        );

        assertThatValueChanges(
                () -> todoRepository.find(todo.getId()).get().isComplete(),
                false,
                true,
                () -> completeTodo.perform(todo.getId())
        );
    }

    @Test
    public void whenTodoExists_awardsPlayerExperience() {
        SavedTodo todo = todoRepository.save(
                new UnsavedTodo(existingPlayer.getId(), "test todo", Todo.Status.INCOMPLETE)
        );

        assertThatValueIncreases(
                () -> playerRepository.find(existingPlayer.getId()).get().getExperience(),
                () -> completeTodo.perform(todo.getId())
        );
    }

    @Test
    public void whenTodoDoesNotExist_throwsException() {
        try{
            completeTodo.perform(new TodoId("no-such-id"));
            fail("Expected a NoSuchTodoException to be thrown");
        } catch(NoSuchTodoException e) {
            assertThat(e.getTodoId(), equalTo(new TodoId("no-such-id")));
        }
    }

}
package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.players.*;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

public class TodosTest {

    private final TodoRepository todoRepository = new InMemoryTodoRepository();
    private final PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final CreateTodoUseCase createTodo = new CreateTodoUseCase(todoRepository);
    private final CompleteTodoUseCase completeTodo = new CompleteTodoUseCase(todoRepository, playerRepository);
    private final AdjustTodoUrgencyUseCase adjustTodoUrgency = new AdjustTodoUrgencyUseCase(todoRepository);
    private final FetchTodosUseCase fetchTodos = new FetchTodosUseCase(todoRepository);

    @Test
    public void creatingAndCompletingTodos() throws NoSuchTodoException {
        SavedPlayer player = playerRepository.save(new UnsavedPlayer("testplayer"));
        assertThat(playerRepository.find(player.getId()).get().getExperience(), equalTo(0));

        assertThat(todoRepository.allTodos().size(), equalTo(0));
        SavedTodo savedTodo = createTodo.perform(player.getId(), "Work on Elementals");
        assertThat(todoRepository.allTodos().size(), equalTo(1));

        assertThat(todoRepository.find(savedTodo.getId()).get().getTitle(), equalTo("Work on Elementals"));
        assertThat(todoRepository.find(savedTodo.getId()).get().getStatus(), equalTo(Todo.Status.INCOMPLETE));

        SavedTodo updatedTodo = completeTodo.perform(savedTodo.getId());

        assertThat(todoRepository.find(savedTodo.getId()).get().getStatus(), equalTo(Todo.Status.COMPLETE));
        assertThat(updatedTodo.getStatus(), equalTo(Todo.Status.COMPLETE));
        assertThat(playerRepository.find(player.getId()).get().getExperience(), greaterThan(0));
    }

    @Test
    public void handlingTodosWithDueDates() throws Exception {
        SavedPlayer player = playerRepository.save(new UnsavedPlayer("testplayer"));

        SavedTodo todoThatNeverGetsDone = createTodo.perform(
                player.getId(),
                "Todo that never gets done",
                LocalDateTime.of(2016, 11, 5, 23, 59, 59)
        );

        SavedTodo todoThatGetsDoneInTime = createTodo.perform(
                player.getId(),
                "Todo that gets done in time",
                LocalDateTime.of(2016, 11, 5, 23, 59, 59)
        );

        assertThat(todoThatNeverGetsDone.getUrgency(), equalTo(Todo.Urgency.DEFAULT));
        assertThat(todoThatGetsDoneInTime.getUrgency(), equalTo(Todo.Urgency.DEFAULT));

        adjustTodoUrgency.perform(todoThatNeverGetsDone.getId(), LocalDateTime.of(2016, 11, 4, 23, 59, 59));
        adjustTodoUrgency.perform(todoThatGetsDoneInTime.getId(), LocalDateTime.of(2016, 11, 4, 23, 59, 59));

        assertThat(todoRepository.find(todoThatNeverGetsDone.getId()).get().getUrgency(), equalTo(Todo.Urgency.DEFAULT));
        assertThat(todoRepository.find(todoThatGetsDoneInTime.getId()).get().getUrgency(), equalTo(Todo.Urgency.DEFAULT));

        adjustTodoUrgency.perform(todoThatNeverGetsDone.getId(), LocalDateTime.of(2016, 11, 5, 23, 59, 59));
        adjustTodoUrgency.perform(todoThatGetsDoneInTime.getId(), LocalDateTime.of(2016, 11, 5, 23, 59, 59));

        assertThat(todoRepository.find(todoThatNeverGetsDone.getId()).get().getUrgency(), equalTo(Todo.Urgency.DEFAULT));
        assertThat(todoRepository.find(todoThatGetsDoneInTime.getId()).get().getUrgency(), equalTo(Todo.Urgency.DEFAULT));

        completeTodo.perform(todoThatGetsDoneInTime.getId());

        adjustTodoUrgency.perform(todoThatNeverGetsDone.getId(), LocalDateTime.of(2016, 11, 6, 0, 0, 0));
        adjustTodoUrgency.perform(todoThatGetsDoneInTime.getId(), LocalDateTime.of(2016, 11, 6, 0, 0, 0));

        assertThat(todoRepository.find(todoThatNeverGetsDone.getId()).get().getUrgency(), equalTo(Todo.Urgency.PAST_DUE));
        assertThat(todoRepository.find(todoThatGetsDoneInTime.getId()).get().getUrgency(), equalTo(Todo.Urgency.DEFAULT));
    }

    @Test
    public void fetchingTodos() throws Exception {
        SavedPlayer player = playerRepository.save(new UnsavedPlayer("testplayer"));

        SavedTodo todo1 = createTodo.perform(player.getId(), "Todo #1");
        SavedTodo todo2 = createTodo.perform(player.getId(), "Todo #2", LocalDateTime.of(2016, 11, 5, 12, 35, 59));
        SavedTodo todo3 = createTodo.perform(player.getId(), "Todo #3");

        List<SavedTodo> todos = fetchTodos.perform();

        assertThat(todos, hasItems(todo1, todo2, todo3));
    }

    @Test
    public void completingATodo_whenNoSuchTodoExists() throws Exception {
        try{
            completeTodo.perform(new TodoId("no-such-id"));
            fail("Expected a NoSuchTodoException to be thrown");
        } catch(NoSuchTodoException e) {
            assertThat(e.getTodoId(), equalTo(new TodoId("no-such-id")));
        }
    }

    @Test
    public void adjustingUrgencyForATodo_whenNoSuchTodoExists() throws Exception {
        try{
            adjustTodoUrgency.perform(new TodoId("no-such-id"), LocalDateTime.now());
            fail("Expected a NoSuchTodoException to be thrown");
        } catch(NoSuchTodoException e) {
            assertThat(e.getTodoId(), equalTo(new TodoId("no-such-id")));
        }
    }
}

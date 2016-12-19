package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.players.PlayerId;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public abstract class TodoRepositoryTest {
    protected abstract TodoRepository repository();

    private PlayerId playerId = new PlayerId("player-id");

    @Test
    public void saveAndFetch() throws Exception {
        UnsavedTodo unsavedTodo1 = new UnsavedTodo(
                playerId,
                "Title #1",
                Todo.Status.INCOMPLETE,
                LocalDateTime.of(2016, 11, 5, 12, 35, 56)
        );
        UnsavedTodo unsavedTodo2 = new UnsavedTodo(
                playerId,
                "Title #2",
                Todo.Status.COMPLETE,
                LocalDateTime.of(2016, 12, 21, 12, 35, 56)
        );
        SavedTodo savedTodo1 = repository().save(unsavedTodo1);
        SavedTodo savedTodo2 = repository().save(unsavedTodo2);

        assertThat(savedTodo1.getId(), not(equalTo(savedTodo2.getId())));

        assertThat(repository().find(savedTodo1.getId()).get().getTitle(), equalTo("Title #1"));
        assertThat(repository().find(savedTodo1.getId()).get().getStatus(), equalTo(Todo.Status.INCOMPLETE));
        assertThat(repository().find(savedTodo1.getId()).get().getDeadline().get(), equalTo(LocalDateTime.of(2016, 11, 5, 12, 35, 56)));

        assertThat(repository().find(savedTodo2.getId()).get().getTitle(), equalTo("Title #2"));
        assertThat(repository().find(savedTodo2.getId()).get().getStatus(), equalTo(Todo.Status.COMPLETE));
        assertThat(repository().find(savedTodo2.getId()).get().getDeadline().get(), equalTo(LocalDateTime.of(2016, 12, 21, 12, 35, 56)));
    }

    @Test
    public void fetchingReturnsCopiesOfTheEntityFromStorage() throws Exception {
        UnsavedTodo unsavedTodo = new UnsavedTodo(playerId, "Original title", Todo.Status.INCOMPLETE);
        SavedTodo savedTodo = repository().save(unsavedTodo);

        savedTodo.setTitle("CORRUPTED");
        // changes not saved

        SavedTodo fetchedTodo = repository().find(savedTodo.getId()).get();
        assertThat(fetchedTodo.getTitle(), equalTo("Original title"));

        repository().save(savedTodo);

        savedTodo.setTitle("CORRUPTED AGAIN");
        assertThat(repository().find(savedTodo.getId()).get().getTitle(), equalTo("CORRUPTED"));
    }

    @Test
    public void update() throws Exception {
        UnsavedTodo unsavedTodo = new UnsavedTodo(playerId, "Original title", Todo.Status.INCOMPLETE);
        SavedTodo savedTodo = repository().save(unsavedTodo);

        savedTodo.setTitle("Some other title");
        savedTodo.setStatus(Todo.Status.COMPLETE);

        SavedTodo updatedTodo = repository().save(savedTodo);
        SavedTodo fetchedTodo = repository().find(savedTodo.getId()).get();

        assertThat(updatedTodo.getTitle(), equalTo("Some other title"));
        assertThat(updatedTodo.getStatus(), equalTo(Todo.Status.COMPLETE));
        assertThat(fetchedTodo.getTitle(), equalTo("Some other title"));
        assertThat(fetchedTodo.getStatus(), equalTo(Todo.Status.COMPLETE));
    }

    @Test
    public void all() throws Exception {
        assertThat(repository().allTodos().size(), equalTo(0));

        UnsavedTodo unsavedTodo1 = new UnsavedTodo(playerId, "Title #1", Todo.Status.INCOMPLETE);
        SavedTodo savedTodo1 = repository().save(unsavedTodo1);
        assertThat(repository().allTodos().size(), equalTo(1));
        assertThat(repository().allTodos(), hasItem(savedTodo1));

        UnsavedTodo unsavedTodo2 = new UnsavedTodo(playerId, "Title #2", Todo.Status.INCOMPLETE);
        SavedTodo savedTodo2 = repository().save(unsavedTodo2);
        assertThat(repository().allTodos().size(), equalTo(2));
        assertThat(repository().allTodos(), hasItem(savedTodo2));
    }
}
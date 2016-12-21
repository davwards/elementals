package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.todos.*;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;
import com.davwards.elementals.game.inmemorypersistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.inmemorypersistence.InMemoryTodoRepository;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.davwards.elementals.TestUtils.assertThatValueChanges;
import static com.davwards.elementals.TestUtils.assertThatValueDecreases;
import static com.davwards.elementals.TestUtils.assertThatValueDoesNotChange;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class UpdateTodoStatusTest {

    private TodoRepository todoRepository = new InMemoryTodoRepository();
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private UpdateTodoStatus updateTodoStatus = new UpdateTodoStatus(todoRepository, playerRepository);

    private SavedPlayer player = playerRepository.save(new UnsavedPlayer("test-player"));
    private LocalDateTime currentTime = LocalDateTime.of(2016, 11, 5, 14, 35, 59);

    private SavedTodo incompleteTodoDueLater = todoRepository.save(
            new UnsavedTodo(player.getId(), "Incomplete todo due later", Todo.Status.INCOMPLETE, currentTime.plusMinutes(5)));
    private SavedTodo completeTodoDueLater = todoRepository.save(
            new UnsavedTodo(player.getId(), "Complete todo due later", Todo.Status.COMPLETE, currentTime.plusMinutes(5)));

    private SavedTodo incompleteTodoDueNow = todoRepository.save(
            new UnsavedTodo(player.getId(), "Incomplete todo due now", Todo.Status.INCOMPLETE, currentTime));
    private SavedTodo completeTodoDueNow = todoRepository.save(
            new UnsavedTodo(player.getId(), "Complete todo due now", Todo.Status.COMPLETE, currentTime));

    private SavedTodo incompleteTodoDueEarlier = todoRepository.save(
            new UnsavedTodo(player.getId(), "Incomplete todo due earlier", Todo.Status.INCOMPLETE, currentTime.minusMinutes(5)));
    private SavedTodo completeTodoDueEarlier = todoRepository.save(
            new UnsavedTodo(player.getId(), "Complete todo due earlier", Todo.Status.COMPLETE, currentTime.minusMinutes(5)));
    private SavedTodo pastDueTodoDueEarlier = todoRepository.save(
            new UnsavedTodo(player.getId(), "Past due todo due earlier", Todo.Status.PAST_DUE, currentTime.minusMinutes(5)));

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotChangeStatus() throws Exception {
        assertThatValueDoesNotChange(
                () -> todoRepository.find(incompleteTodoDueLater.getId()).get().getStatus(),
                () -> updateTodoStatus.perform(incompleteTodoDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> todoRepository.find(completeTodoDueLater.getId()).get().getStatus(),
                () -> updateTodoStatus.perform(completeTodoDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> todoRepository.find(incompleteTodoDueNow.getId()).get().getStatus(),
                () -> updateTodoStatus.perform(incompleteTodoDueNow.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> todoRepository.find(completeTodoDueNow.getId()).get().getStatus(),
                () -> updateTodoStatus.perform(completeTodoDueNow.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_changesIncompleteTodosToPastDue() throws Exception {
        assertThatValueChanges(
                () -> todoRepository.find(incompleteTodoDueEarlier.getId()).get().getStatus(),
                Todo.Status.INCOMPLETE,
                Todo.Status.PAST_DUE,
                () -> updateTodoStatus.perform(incompleteTodoDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> todoRepository.find(completeTodoDueEarlier.getId()).get().getStatus(),
                () -> updateTodoStatus.perform(completeTodoDueEarlier.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotDamagePlayer() throws Exception {
        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(incompleteTodoDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(completeTodoDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(incompleteTodoDueNow.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(completeTodoDueNow.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_damagesPlayerForIncompleteTodos() throws Exception {
        assertThatValueDecreases(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(incompleteTodoDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(completeTodoDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTodoStatus.perform(pastDueTodoDueEarlier.getId(), currentTime)
        );
    }

    @Test
    public void whenTodoDoesNotExist_throwsException() {
        try{
            updateTodoStatus.perform(new TodoId("no-such-id"), currentTime);
            fail("Expected a NoSuchTodoException to be thrown");
        } catch(NoSuchTodoException e) {
            assertThat(e.getTodoId(), equalTo(new TodoId("no-such-id")));
        }
    }
}
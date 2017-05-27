package com.davwards.elementals.game;

import com.davwards.elementals.game.players.PlayerId;
import com.davwards.elementals.game.fakes.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.*;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class FetchTaskTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private FetchTask fetchTask = new FetchTask(taskRepository);

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() throws Exception {
        SavedTask task = taskRepository.save(ImmutableUnsavedTask.builder()
                .playerId(new PlayerId("some-player"))
                .title("the title")
                .status(Task.Status.INCOMPLETE)
                .build());

        assertThat(
                fetchTask.perform(
                        task.getId(),
                        Function.identity(),
                        () -> null),
                equalTo(task)
        );
    }

    @Test
    public void whenTaskDoesNotExist_returnsResultOfNoSuchTaskMapper() throws Exception {

        String result = fetchTask.perform(
                new TaskId("nonsense-id"),
                task -> "found the task",
                () -> "no such task"
        );

        assertThat(result, equalTo("no such task"));
    }
}
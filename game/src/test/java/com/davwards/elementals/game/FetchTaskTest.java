package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class FetchTaskTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private FetchTask fetchTask = new FetchTask(taskRepository);

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() throws Exception {
        SavedTask task =
                taskRepository.save(
                        new UnsavedTask(
                                new PlayerId("some-player"),
                                "the title",
                                Task.Status.INCOMPLETE
                        )
                );

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
package com.davwards.elementals.game;

import com.davwards.elementals.game.players.PlayerId;
import com.davwards.elementals.game.fakes.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.*;
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
        SavedTask task = taskRepository.save(ImmutableUnsavedTask.builder()
                .playerId(new PlayerId("some-player"))
                .title("the title")
                .status(Task.Status.INCOMPLETE)
                .build());

        assertThat(
                fetchTask.perform(
                        task.getId(),
                        new FetchTask.Outcome<SavedTask>() {
                            @Override
                            public SavedTask successfullyFetchedTask(SavedTask task) {
                                return task;
                            }

                            @Override
                            public SavedTask noSuchTask() {
                                fail("Expected successfullyFetchedTask outcome");
                                return null;
                            }
                        }),
                equalTo(task)
        );
    }

    @Test
    public void whenTaskDoesNotExist_returnsResultOfNoSuchTask() throws Exception {

        String result = fetchTask.perform(
                new TaskId("nonsense-id"),
                new FetchTask.Outcome<String>() {
                    @Override
                    public String successfullyFetchedTask(SavedTask task) {
                        fail("Expected noSuchTask outcome");
                        return null;
                    }

                    @Override
                    public String noSuchTask() {
                        return "no such task";
                    }
                }
        );

        assertThat(result, equalTo("no such task"));
    }
}
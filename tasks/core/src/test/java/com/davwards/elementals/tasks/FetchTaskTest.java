package com.davwards.elementals.tasks;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.Task;
import com.davwards.elementals.tasks.models.TaskId;
import com.davwards.elementals.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.tasks.persistence.TaskRepository;
import org.junit.Test;

import static com.davwards.elementals.support.test.Factories.randomUnsavedTask;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class FetchTaskTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private FetchTask fetchTask = new FetchTask(taskRepository);

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() throws Exception {
        SavedTask task = taskRepository.save(randomUnsavedTask()
                .withPlayerId(new PlayerId("some-player"))
                .withTitle("the title")
                .withStatus(Task.Status.INCOMPLETE));

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
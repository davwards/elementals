package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepositoryTest;
import com.davwards.elementals.game.tasks.models.*;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.davwards.elementals.support.test.Factories.randomUnsavedTask;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class TaskRepositoryTest extends
        CrudRepositoryTest<TaskRepository, TaskId, UnsavedTask, SavedTask> {
    protected abstract TaskRepository repository();

    @Test
    public void findByPlayer() throws Exception {
        PlayerId matchingPlayerId = new PlayerId("matching");
        PlayerId nonmatchingPlayerId = new PlayerId("nonmatching");

        SavedTask matchingTask1 = repository().save(randomUnsavedTask()
                .withPlayerId(matchingPlayerId));
        SavedTask matchingTask2 = repository().save(randomUnsavedTask()
                .withPlayerId(matchingPlayerId));
        SavedTask nonmatchingTask = repository().save(randomUnsavedTask()
                .withPlayerId(nonmatchingPlayerId));

        List<SavedTask> results = repository().findByPlayerId(matchingPlayerId);

        assertThat(results, hasItem(matchingTask1));
        assertThat(results, hasItem(matchingTask2));
        assertThat(results, not(hasItem(nonmatchingTask)));
    }

    @Test
    public void findByParentRecurringTaskId() {
        RecurringTaskId matchingRecurringTaskId = new RecurringTaskId("matching");
        RecurringTaskId nonmatchingRecurringTaskId = new RecurringTaskId("nonmatching");

        SavedTask matchingTask1 = repository().save(randomUnsavedTask()
                .withParentRecurringTaskId(matchingRecurringTaskId));
        SavedTask matchingTask2 = repository().save(randomUnsavedTask()
                .withParentRecurringTaskId(matchingRecurringTaskId));
        SavedTask nonmatchingTask1 = repository().save(randomUnsavedTask()
                .withParentRecurringTaskId(nonmatchingRecurringTaskId));
        SavedTask nonmatchingTask2 = repository().save(randomUnsavedTask()
                .withParentRecurringTaskId(Optional.empty()));

        List<SavedTask> results = repository().findByParentRecurringTaskId(matchingRecurringTaskId);

        assertThat(results, hasItem(matchingTask1));
        assertThat(results, hasItem(matchingTask2));
        assertThat(results, not(hasItem(nonmatchingTask1)));
        assertThat(results, not(hasItem(nonmatchingTask2)));
    }

    @Override
    protected UnsavedTask givenAnUnsavedRecord() {
        return randomUnsavedTask();
    }

    @Override
    protected SavedTask whenASavedRecordIsModified(SavedTask original) {
        return SavedTask.copy(original).withTitle("Updated " + original.title());
    }

    @Override
    protected void assertIdentical(UnsavedTask original, SavedTask saved) {
        assertThat(original, equalTo(ImmutableUnsavedTask.builder().from(saved).build()));
    }

    @Override
    protected void assertIdentical(SavedTask original, SavedTask saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedTask left, SavedTask right) {
        assertThat(left, not(equalTo(right)));
    }
}

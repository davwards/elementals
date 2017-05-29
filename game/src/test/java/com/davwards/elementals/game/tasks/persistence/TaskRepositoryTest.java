package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.CrudRepositoryTest;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;
import org.junit.Test;

import java.util.List;

import static com.davwards.elementals.game.support.test.Factories.randomUnsavedPlayer;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedTask;
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

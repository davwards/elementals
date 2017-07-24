package com.davwards.elementals.tasks.persistence;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepositoryTest;
import com.davwards.elementals.tasks.models.ImmutableUnsavedRecurringTask;
import com.davwards.elementals.tasks.models.RecurringTaskId;
import com.davwards.elementals.tasks.models.SavedRecurringTask;
import com.davwards.elementals.tasks.models.UnsavedRecurringTask;
import org.junit.Test;

import java.util.List;

import static com.davwards.elementals.support.test.Factories.randomUnsavedRecurringTask;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class RecurringTaskRepositoryTest extends
        CrudRepositoryTest<RecurringTaskRepository, RecurringTaskId, UnsavedRecurringTask, SavedRecurringTask> {
    protected abstract RecurringTaskRepository repository();

    @Test
    public void findByPlayer() throws Exception {
        PlayerId matchingPlayerId = new PlayerId("matching");
        PlayerId nonmatchingPlayerId = new PlayerId("nonmatching");

        SavedRecurringTask matchingTask1 = repository().save(randomUnsavedRecurringTask()
                .withPlayerId(matchingPlayerId));
        SavedRecurringTask matchingTask2 = repository().save(randomUnsavedRecurringTask()
                .withPlayerId(matchingPlayerId));
        SavedRecurringTask nonmatchingTask = repository().save(randomUnsavedRecurringTask()
                .withPlayerId(nonmatchingPlayerId));

        List<SavedRecurringTask> results = repository().findByPlayerId(matchingPlayerId);

        assertThat(results, hasItem(matchingTask1));
        assertThat(results, hasItem(matchingTask2));
        assertThat(results, not(hasItem(nonmatchingTask)));
    }

    @Override
    protected UnsavedRecurringTask givenAnUnsavedRecord() {
        return randomUnsavedRecurringTask();
    }

    @Override
    protected SavedRecurringTask whenASavedRecordIsModified(SavedRecurringTask original) {
        return SavedRecurringTask.copy(original).withTitle("Updated " + original.title());
    }

    @Override
    protected void assertIdentical(UnsavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(
                original,
                equalTo(ImmutableUnsavedRecurringTask.builder().from(saved).build())
        );
    }

    @Override
    protected void assertIdentical(SavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedRecurringTask left, SavedRecurringTask right) {
        assertThat(left, not(equalTo(right)));
    }
}

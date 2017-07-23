package com.davwards.elementals.game.tasks.recurring;

import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class FetchRecurringTask {
    public interface Outcome<T> {
        T successfullyFetchedRecurringTask(SavedRecurringTask task);
        T noSuchRecurringTask();
    }

    public <T> T perform(RecurringTaskId id, Outcome<T> outcome) {
        return strict(recurringTaskRepository.find(id))
                .map(outcome::successfullyFetchedRecurringTask)
                .orElseGet(outcome::noSuchRecurringTask);
    }

    private final RecurringTaskRepository recurringTaskRepository;

    public FetchRecurringTask(RecurringTaskRepository recurringTaskRepository) {
        this.recurringTaskRepository = recurringTaskRepository;
    }
}

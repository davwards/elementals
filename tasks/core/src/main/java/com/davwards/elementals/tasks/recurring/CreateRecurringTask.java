package com.davwards.elementals.tasks.recurring;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.tasks.models.ImmutableUnsavedRecurringTask;
import com.davwards.elementals.tasks.models.SavedRecurringTask;
import com.davwards.elementals.tasks.persistence.RecurringTaskRepository;

import java.time.Period;

public class CreateRecurringTask {
    public interface Outcome<T> {
        T successfullyCreatedRecurringTask(SavedRecurringTask createdTask);
    }

    public <T> T perform(String title,
                         PlayerId playerId,
                         String cadence,
                         Period duration,
                         Outcome<T> outcome) {

        return outcome.successfullyCreatedRecurringTask(
                recurringTaskRepository.save(
                        ImmutableUnsavedRecurringTask.builder()
                                .title(title)
                                .playerId(playerId)
                                .cadence(cadence)
                                .duration(duration)
                                .build()
                )
        );
    }

    private final RecurringTaskRepository recurringTaskRepository;

    public CreateRecurringTask(RecurringTaskRepository recurringTaskRepository) {
        this.recurringTaskRepository = recurringTaskRepository;
    }
}

package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;

import java.time.Period;

public class CreateRecurringTask {
    public interface Outcome<T> {
        T successfullyCreatedRecurringTask(SavedRecurringTask createdTask);
    }

    public <T> T perform(String title,
                         PlayerId playerId,
                         String cadence,
                         Period duration,
                         Outcome<T> handle) {

        return handle.successfullyCreatedRecurringTask(
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

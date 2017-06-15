package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.util.List;

public class GetPlayerDetails {
    public interface Outcome<T> {
        T foundTasks(List<SavedTask> tasks, List<SavedRecurringTask> recurringTasks);
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        return outcome.foundTasks(
                taskRepository.findByPlayerId(playerId),
                recurringTaskRepository.findByPlayerId(playerId)
        );
    }

    private final TaskRepository taskRepository;
    private final RecurringTaskRepository recurringTaskRepository;

    public GetPlayerDetails(TaskRepository taskRepository, RecurringTaskRepository recurringTaskRepository) {
        this.taskRepository = taskRepository;
        this.recurringTaskRepository = recurringTaskRepository;
    }
}

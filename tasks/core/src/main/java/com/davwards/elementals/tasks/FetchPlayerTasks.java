package com.davwards.elementals.tasks;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.persistence.TaskRepository;

import java.util.List;

public class FetchPlayerTasks {
    public interface Outcome<T> {
        T foundTasks(List<SavedTask> tasks);
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        return outcome.foundTasks(taskRepository.findByPlayerId(playerId));
    }

    private final TaskRepository taskRepository;

    public FetchPlayerTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}

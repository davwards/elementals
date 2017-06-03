package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class FetchTask {
    public interface Outcome<T> {
        T successfullyFetchedTask(SavedTask task);
        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> handle) {
        return strict(taskRepository.find(id))
                .map(handle::successfullyFetchedTask)
                .orElseGet(handle::noSuchTask);
    }

    private final TaskRepository taskRepository;

    public FetchTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}

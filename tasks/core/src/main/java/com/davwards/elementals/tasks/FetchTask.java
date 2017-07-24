package com.davwards.elementals.tasks;

import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.TaskId;
import com.davwards.elementals.tasks.persistence.TaskRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class FetchTask {
    public interface Outcome<T> {
        T successfullyFetchedTask(SavedTask task);
        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> outcome) {
        return strict(taskRepository.find(id))
                .map(outcome::successfullyFetchedTask)
                .orElseGet(outcome::noSuchTask);
    }

    private final TaskRepository taskRepository;

    public FetchTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}

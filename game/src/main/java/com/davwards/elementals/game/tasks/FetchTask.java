package com.davwards.elementals.game.tasks;

public class FetchTask {
    public interface Outcome<T> {
        T successfullyFetchedTask(SavedTask task);
        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> handle) {
        return taskRepository
                .find(id)
                .map(handle::successfullyFetchedTask)
                .orElseGet(handle::noSuchTask);
    }

    private final TaskRepository taskRepository;

    public FetchTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}

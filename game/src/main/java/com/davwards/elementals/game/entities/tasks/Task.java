package com.davwards.elementals.game.entities.tasks;

import com.davwards.elementals.game.entities.players.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;

public class Task {
    protected String title;
    protected Status status;
    private Optional<LocalDateTime> deadline = Optional.empty();
    private PlayerId playerId;

    public Task(PlayerId playerId, String title, Status status) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
    }

    public Task(PlayerId playerId, String title, Status status, LocalDateTime deadline) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
        this.deadline = Optional.of(deadline);
    }

    public Task(PlayerId playerId, String title, Status status, Optional<LocalDateTime> deadline) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        return title.equals(task.title) && status == task.status && deadline == task.deadline;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + deadline.hashCode();
        return result;
    }

    public Optional<LocalDateTime> getDeadline() {
        return deadline;
    }

    public boolean isComplete() {
        return this.getStatus().equals(Status.COMPLETE);
    }

    public boolean isIncomplete() {
        return this.getStatus().equals(Status.INCOMPLETE);
    }

    public PlayerId getPlayerId() {
        return playerId;
    }

    public enum Status {COMPLETE, PAST_DUE, INCOMPLETE}
}

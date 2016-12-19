package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.players.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;

public class Todo {
    protected String title;
    protected Status status;
    protected Urgency urgency = Urgency.DEFAULT;
    private Optional<LocalDateTime> deadline = Optional.empty();
    private PlayerId playerId;

    public Todo(PlayerId playerId, String title, Status status) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
    }

    public Todo(PlayerId playerId, String title, Status status, LocalDateTime deadline) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
        this.deadline = Optional.of(deadline);
    }

    public Todo(PlayerId playerId, String title, Status status, Urgency urgency, Optional<LocalDateTime> deadline) {
        this.playerId = playerId;
        this.title = title;
        this.status = status;
        this.deadline = deadline;
        this.urgency = urgency;
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

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo)) return false;

        Todo todo = (Todo) o;

        return title.equals(todo.title) && status == todo.status && urgency == todo.urgency;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + urgency.hashCode();
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

    public enum Status {COMPLETE, INCOMPLETE}

    public enum Urgency {PAST_DUE, DEFAULT}
}

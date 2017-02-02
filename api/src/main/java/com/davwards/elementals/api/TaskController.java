package com.davwards.elementals.api;

import com.davwards.elementals.game.CompleteTask;
import com.davwards.elementals.game.CreateTask;
import com.davwards.elementals.game.FetchTask;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.Task;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@RestController
public class TaskController {

    private final CreateTask createTask;
    private final FetchTask fetchTask;
    private final CompleteTask completeTask;

    public TaskController(CreateTask createTask, FetchTask fetchTask, CompleteTask completeTask) {
        this.createTask = createTask;
        this.fetchTask = fetchTask;
        this.completeTask = completeTask;
    }

    @RequestMapping(value = "/api/players/{playerId}/tasks", method = RequestMethod.POST)
    public ResponseEntity createTaskForPlayer(UriComponentsBuilder uriBuilder,
                                              @PathVariable("playerId") String playerId,
                                              @RequestBody CreateTaskRequest createTaskRequest) {

        SavedTask task = createTask.perform(
                new PlayerId(playerId),
                createTaskRequest.getTitle(),
                LocalDateTime.parse(createTaskRequest.getDeadline())
        );

        return ResponseEntity.created(
                uriBuilder.path("/api/tasks/" + task.getId()).build().toUri()
        ).body(responseFor(task));
    }

    @RequestMapping(value = "api/tasks/{id}", method = RequestMethod.GET)
    public ResponseEntity getTask(@PathVariable("id") String id) {
        return fetchTask.perform(
                new TaskId(id),
                task -> ResponseEntity.ok(responseFor(task)),
                () -> ResponseEntity.notFound().build()
        );
    }

    @RequestMapping(value = "/api/tasks/{id}/complete", method = RequestMethod.PUT)
    public ResponseEntity completeTask(@PathVariable("id") String id) {
        return completeTask.perform(
                new TaskId(id),
                task -> ResponseEntity.ok(responseFor(task)),
                () -> ResponseEntity.notFound().build()
        );
    }

    private TaskResponse responseFor(SavedTask task) {
        return new TaskResponse(
                task.getTitle(),
                task.getDeadline()
                        .map(deadline -> deadline.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .orElse(null),
                task.getPlayerId().toString(),
                statusValue(task.getStatus())
        );
    }

    private String statusValue(Task.Status status) {
        switch (status) {
            case COMPLETE:
                return "complete";
            case INCOMPLETE:
                return "incomplete";
            case PAST_DUE:
                return "pastDue";
            default:
                throw new RuntimeException("Could not translate unknown Status value: " + status);
        }
    }

    private static class CreateTaskRequest {
        private String title;
        private String deadline;

        public String getDeadline() {
            return deadline;
        }

        public String getTitle() {
            return title;
        }
    }

    private static class TaskResponse {
        @JsonProperty
        private final String title;

        @JsonProperty
        private final String deadline;

        @JsonProperty
        private final String playerId;

        @JsonProperty
        private final String status;

        private TaskResponse(String title, String deadline, String playerId, String status) {
            this.title = title;
            this.deadline = deadline;
            this.playerId = playerId;
            this.status = status;
        }
    }
}

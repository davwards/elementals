package com.davwards.elementals.api.tasks;

import com.davwards.elementals.api.support.responses.ResourceCreatedResponses;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.CreateTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
public class CreateTaskEndpoint {

    private static class CreateTaskRequest {
        @JsonProperty
        private String title;
        @JsonProperty
        private String deadline;
    }

    private static class PossibleResponses extends ResourceCreatedResponses<SavedTask>
            implements CreateTask.Outcome<ResponseEntity> {

        @Override
        public ResponseEntity successfullyCreatedTask(SavedTask createdTask) {
            return ResponseEntity
                    .created(resourceLocation(createdTask))
                    .body(new TaskResponse(createdTask));
        }

        private PossibleResponses(UriComponentsBuilder uriBuilder) {
            super(uriBuilder, "api/tasks");
        }

    }

    @RequestMapping(
            value = "/api/players/{playerId}/tasks",
            method = RequestMethod.POST)
    public ResponseEntity createTaskForPlayer(
            UriComponentsBuilder uriBuilder,
            @PathVariable("playerId") String playerId,
            @RequestBody CreateTaskRequest createTaskRequest
    ) {
        return createTask.perform(
                new PlayerId(playerId),
                createTaskRequest.title,
                LocalDateTime.parse(createTaskRequest.deadline),
                new PossibleResponses(uriBuilder)
        );
    }

    private final CreateTask createTask;

    public CreateTaskEndpoint(CreateTask createTask) {
        this.createTask = createTask;
    }
}

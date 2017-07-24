package com.davwards.elementals.api.tasks;

import com.davwards.elementals.support.api.ErrorResponse;
import com.davwards.elementals.support.api.ResourceCreatedResponses;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.language.Either;
import com.davwards.elementals.tasks.CreateTask;
import com.davwards.elementals.tasks.models.SavedTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Function;

import static com.davwards.elementals.support.language.Either.failure;
import static com.davwards.elementals.support.language.Either.success;

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

        ResponseEntity malformedDeadline(DateTimeParseException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
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
            @RequestBody CreateTaskRequest createTaskRequest) {

        return Optional.ofNullable(createTaskRequest.deadline)
                .map(createTaskWithDeadline())
                .orElse(createTaskWithoutDeadline())
                .applyRequiredParameters(
                        new PlayerId(playerId),
                        createTaskRequest.title,
                        new PossibleResponses(uriBuilder)
                );
    }

    private final CreateTask createTask;

    public CreateTaskEndpoint(CreateTask createTask) {
        this.createTask = createTask;
    }

    private ApplyRequiredParameters createTaskWithoutDeadline() {
        return createTask::perform;
    }

    private Function<String, ApplyRequiredParameters> createTaskWithDeadline() {

        return deadline -> (playerId, title, possibleResponses) ->
                parseDeadline(deadline)
                        .map(parsedDeadline ->
                                createTask.perform(
                                        playerId,
                                        title,
                                        parsedDeadline,
                                        possibleResponses
                                ))
                        .orIfFailure(possibleResponses::malformedDeadline);
    }

    private Either<LocalDateTime, DateTimeParseException> parseDeadline(String deadline) {
        try {
            return success(LocalDateTime.parse(deadline));
        } catch (DateTimeParseException e) {
            return failure(e);
        }
    }

    private interface ApplyRequiredParameters {
        ResponseEntity applyRequiredParameters(
                PlayerId playerId,
                String title,
                PossibleResponses possibleResponses
        );
    }
}

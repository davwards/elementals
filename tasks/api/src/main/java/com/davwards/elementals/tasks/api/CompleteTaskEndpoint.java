package com.davwards.elementals.tasks.api;

import com.davwards.elementals.tasks.CompleteTask;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.TaskId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompleteTaskEndpoint {

    private static class PossibleResponses implements CompleteTask.Outcome<ResponseEntity> {
        @Override
        public ResponseEntity taskSuccessfullyCompleted(SavedTask completedTask) {
            return ResponseEntity
                    .ok(new TaskResponse(completedTask));
        }

        @Override
        public ResponseEntity noSuchTask() {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @RequestMapping(
            value = "/api/tasks/{id}/complete",
            method = RequestMethod.PUT)
    public ResponseEntity completeTask(
            @PathVariable("id") String id) {

        return completeTask.perform(
                new TaskId(id),
                new PossibleResponses()
        );
    }

    private final CompleteTask completeTask;

    public CompleteTaskEndpoint(CompleteTask completeTask) {
        this.completeTask = completeTask;
    }
}

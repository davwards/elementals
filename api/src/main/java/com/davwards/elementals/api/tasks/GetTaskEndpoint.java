package com.davwards.elementals.api.tasks;

import com.davwards.elementals.tasks.FetchTask;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.TaskId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTaskEndpoint {

    @RequestMapping(value = "api/tasks/{id}", method = RequestMethod.GET)
    public ResponseEntity getTask(@PathVariable("id") String id) {
        return fetchTask.perform(
                new TaskId(id),
                new FetchTask.Outcome<ResponseEntity>() {
                    @Override
                    public ResponseEntity successfullyFetchedTask(SavedTask task) {
                        return ResponseEntity.ok(new TaskResponse(task));
                    }

                    @Override
                    public ResponseEntity noSuchTask() {
                        return ResponseEntity.notFound().build();
                    }
                }
        );
    }

    private final FetchTask fetchTask;

    public GetTaskEndpoint(FetchTask fetchTask) {
        this.fetchTask = fetchTask;
    }
}

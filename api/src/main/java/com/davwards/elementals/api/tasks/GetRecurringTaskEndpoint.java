package com.davwards.elementals.api.tasks;

import com.davwards.elementals.tasks.models.RecurringTaskId;
import com.davwards.elementals.tasks.models.SavedRecurringTask;
import com.davwards.elementals.tasks.recurring.FetchRecurringTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetRecurringTaskEndpoint {

    @RequestMapping(value="api/recurring-tasks/{id}", method = RequestMethod.GET)
    public ResponseEntity getRecurringTask(@PathVariable("id") String id) {
        return fetchRecurringTask.perform(
                new RecurringTaskId(id),
                new FetchRecurringTask.Outcome<ResponseEntity>() {
                    @Override
                    public ResponseEntity successfullyFetchedRecurringTask(SavedRecurringTask task) {
                        return ResponseEntity.ok(new RecurringTaskResponse(task));
                    }

                    @Override
                    public ResponseEntity noSuchRecurringTask() {
                        return ResponseEntity.notFound().build();
                    }
                }
        );
    }

    private final FetchRecurringTask fetchRecurringTask;

    public GetRecurringTaskEndpoint(FetchRecurringTask fetchRecurringTask) {
        this.fetchRecurringTask = fetchRecurringTask;
    }
}

package com.davwards.elementals.tasks.api;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.tasks.FetchPlayerTasks;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class GetTasksForPlayerEndpoint {
    @GetMapping("/api/players/{playerId}/tasks")
    public ResponseEntity perform(@PathVariable("playerId") String playerId) {
        return fetchPlayerTasks.perform(new PlayerId(playerId), tasks -> ResponseEntity.ok(
                tasks.stream().map(TaskResponse::new).collect(Collectors.toList())
        ));
    }

    private final FetchPlayerTasks fetchPlayerTasks;

    public GetTasksForPlayerEndpoint(FetchPlayerTasks fetchPlayerTasks) {
        this.fetchPlayerTasks = fetchPlayerTasks;
    }
}

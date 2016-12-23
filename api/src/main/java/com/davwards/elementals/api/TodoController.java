package com.davwards.elementals.api;

import com.davwards.elementals.game.CompleteTodo;
import com.davwards.elementals.game.CreateTodo;
import com.davwards.elementals.game.FetchTodo;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.Todo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class TodoController {

    private final CreateTodo createTodo;
    private final FetchTodo fetchTodo;
    private final CompleteTodo completeTodo;

    public TodoController(CreateTodo createTodo, FetchTodo fetchTodo, CompleteTodo completeTodo) {
        this.createTodo = createTodo;
        this.fetchTodo = fetchTodo;
        this.completeTodo = completeTodo;
    }

    @RequestMapping(value = "/api/players/{playerId}/todos", method = RequestMethod.POST)
    public ResponseEntity createTodoForPlayer(UriComponentsBuilder uriBuilder,
                                              @PathVariable("playerId") String playerId,
                                              @RequestBody CreateTodoRequest createTodoRequest) {

        SavedTodo todo = createTodo.perform(
                new PlayerId(playerId),
                createTodoRequest.getTitle(),
                LocalDateTime.parse(createTodoRequest.getDeadline())
        );

        return ResponseEntity.created(
                uriBuilder.path("/api/todos/" + todo.getId()).build().toUri()
        ).body(wrappedTodoResponseFor(todo));
    }

    @RequestMapping(value = "api/todos/{id}", method = RequestMethod.GET)
    public ResponseEntity getTodo(@PathVariable("id") String id) {
        try {
            SavedTodo todo = fetchTodo.perform(new TodoId(id));
            return ResponseEntity.ok(wrappedTodoResponseFor(todo));
        } catch(NoSuchTodoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/api/todos/{id}/complete", method = RequestMethod.PUT)
    public ResponseEntity completeTodo(@PathVariable("id") String id) {
        try {
            SavedTodo todo = completeTodo.perform(new TodoId(id));
            return ResponseEntity.ok(wrappedTodoResponseFor(todo));
        } catch(NoSuchTodoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private TodoResponse.Envelope wrappedTodoResponseFor(SavedTodo todo) {
        return new TodoResponse.Envelope(
                new TodoResponse(
                        todo.getTitle(),
                        todo.getDeadline()
                                .map(deadline -> deadline.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .orElse(null),
                        todo.getPlayerId().toString(),
                        statusValue(todo.getStatus())
                )
        );
    }

    private String statusValue(Todo.Status status) {
        switch(status) {
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

    private static class CreateTodoRequest {
        private String title;
        private String deadline;

        public String getDeadline() {
            return deadline;
        }

        public String getTitle() {
            return title;
        }
    }

    private static class TodoResponse {
        @JsonProperty
        private final String title;

        @JsonProperty
        private final String deadline;

        @JsonProperty
        private final String playerId;

        @JsonProperty
        private final String status;

        private TodoResponse(String title, String deadline, String playerId, String status) {
            this.title = title;
            this.deadline = deadline;
            this.playerId = playerId;
            this.status = status;
        }

        private static class Envelope {
            @JsonProperty
            private final TodoResponse todo;

            private Envelope(TodoResponse todo) {
                this.todo = todo;
            }
        }
    }
}

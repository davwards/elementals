package com.davwards.elementals.api;

import com.davwards.elementals.game.*;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTodoRepository;
import com.davwards.elementals.scheduler.ManualTimeProvider;
import com.davwards.elementals.scheduler.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        CreatePlayer.class,
        FetchPlayer.class,
        CreateTodo.class,
        FetchTodo.class,
        CompleteTodo.class,
        UpdateTodoStatus.class,
        ResurrectPlayer.class,

        Scheduler.class,

        InMemoryPlayerRepository.class,
        InMemoryTodoRepository.class,

        ManualTimeProvider.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

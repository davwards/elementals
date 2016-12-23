package com.davwards.elementals.api;

import com.davwards.elementals.game.*;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTodoRepository;
import com.davwards.elementals.scheduler.ManualTimeProvider;
import com.davwards.elementals.scheduler.Scheduler;
import com.davwards.elementals.scheduler.TimeProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
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

    @Bean
    @Profile("controlled-time")
    @Primary
    public ManualTimeProvider manualTimeProvider() {
        return new ManualTimeProvider();
    }

    @Bean
    public TimeProvider timeProvider() {
        return LocalDateTime::now;
    }
}

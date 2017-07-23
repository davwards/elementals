package com.davwards.elementals.api;

import com.davwards.elementals.game.habits.CreateHabit;
import com.davwards.elementals.game.habits.ExerciseHabit;
import com.davwards.elementals.game.habits.FetchHabit;
import com.davwards.elementals.game.habits.persistence.InMemoryHabitRepository;
import com.davwards.elementals.game.players.UpdatePlayerCurrencies;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.players.CreatePlayer;
import com.davwards.elementals.game.players.FetchPlayer;
import com.davwards.elementals.game.players.ResurrectPlayer;
import com.davwards.elementals.game.tasks.CompleteTask;
import com.davwards.elementals.game.tasks.CreateTask;
import com.davwards.elementals.game.tasks.FetchTask;
import com.davwards.elementals.game.tasks.UpdateTaskStatus;
import com.davwards.elementals.game.tasks.recurring.CreateRecurringTask;
import com.davwards.elementals.game.tasks.recurring.FetchRecurringTask;
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
        ResurrectPlayer.class,
        UpdatePlayerCurrencies.class,

        CreateTask.class,
        FetchTask.class,
        CompleteTask.class,
        UpdateTaskStatus.class,

        CreateHabit.class,
        ExerciseHabit.class,
        FetchHabit.class,

        CreateRecurringTask.class,
        FetchRecurringTask.class,

        Scheduler.class,

        InMemoryPlayerRepository.class,
        InMemoryTaskRepository.class,
        InMemoryHabitRepository.class,
        InMemoryRecurringTaskRepository.class,

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

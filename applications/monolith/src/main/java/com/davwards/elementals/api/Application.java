package com.davwards.elementals.api;

import com.davwards.elementals.habits.CreateHabit;
import com.davwards.elementals.habits.ExerciseHabit;
import com.davwards.elementals.habits.FetchHabit;
import com.davwards.elementals.habits.persistence.InMemoryHabitRepository;
import com.davwards.elementals.players.*;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.resurrectionscheduler.ResurrectionScheduler;
import com.davwards.elementals.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.tasks.CompleteTask;
import com.davwards.elementals.tasks.CreateTask;
import com.davwards.elementals.tasks.FetchTask;
import com.davwards.elementals.tasks.UpdateTaskStatus;
import com.davwards.elementals.tasks.recurring.CreateRecurringTask;
import com.davwards.elementals.tasks.recurring.FetchRecurringTask;
import com.davwards.elementals.support.scheduling.ManualTimeProvider;
import com.davwards.elementals.support.scheduling.TimeProvider;
import com.davwards.elementals.tasks.updatescheduler.TaskUpdateScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
@Import({
        CreatePlayer.class,
        FetchPlayer.class,
        ResurrectPlayer.class,
        UpdatePlayerCurrencies.class,
        LevelUpPlayer.class,
        CheckWhetherPlayerCanLevelUp.InProcess.class,

        CreateTask.class,
        FetchTask.class,
        CompleteTask.class,
        UpdateTaskStatus.class,

        CreateHabit.class,
        ExerciseHabit.class,
        FetchHabit.class,

        CreateRecurringTask.class,
        FetchRecurringTask.class,

        ResurrectionScheduler.class,
        TaskUpdateScheduler.class,

        InMemoryPlayerRepository.class,
        InMemoryTaskRepository.class,
        InMemoryHabitRepository.class,
        InMemoryRecurringTaskRepository.class,

        ManualTimeProvider.class
})
@ComponentScan(basePackages = {
        "com.davwards.elementals.api",
        "com.davwards.elementals.players.api",
        "com.davwards.elementals.tasks.api",
        "com.davwards.elementals.habits.api"
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

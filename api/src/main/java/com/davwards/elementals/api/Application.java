package com.davwards.elementals.api;

import com.davwards.elementals.game.CreatePlayer;
import com.davwards.elementals.game.FetchPlayer;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTodoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        CreatePlayer.class,
        FetchPlayer.class,

        InMemoryPlayerRepository.class,
        InMemoryTodoRepository.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

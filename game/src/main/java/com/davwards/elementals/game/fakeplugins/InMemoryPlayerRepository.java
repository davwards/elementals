package com.davwards.elementals.game.fakeplugins;

import com.davwards.elementals.game.entities.players.*;

import java.util.*;

public class InMemoryPlayerRepository implements PlayerRepository {
    private Map<PlayerId, SavedPlayer> contents = new HashMap<>();

    @Override
    public SavedPlayer save(UnsavedPlayer player) {
        PlayerId id = new PlayerId(UUID.randomUUID().toString());
        SavedPlayer savedPlayer = ImmutableSavedPlayer.builder()
                .id(id)
                .name(player.name())
                .experience(player.experience())
                .health(player.health())
                .build();

        contents.put(id, savedPlayer);

        return savedPlayer;
    }

    @Override
    public Optional<SavedPlayer> find(PlayerId id) {
        return Optional.ofNullable(contents.get(id));
    }

    @Override
    public List<SavedPlayer> all() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public SavedPlayer update(SavedPlayer player) {
        contents.put(player.getId(), player);
        return player;
    }
}

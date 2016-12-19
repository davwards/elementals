package com.davwards.elementals.game.players;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryPlayerRepository implements PlayerRepository {
    private Map<PlayerId, SavedPlayer> contents = new HashMap<>();

    @Override
    public SavedPlayer save(UnsavedPlayer player) {
        PlayerId id = new PlayerId(UUID.randomUUID().toString());
        SavedPlayer savedPlayer = new SavedPlayer(
                id,
                player.getName(),
                player.getExperience()
        );
        contents.put(id, savedPlayer);

        return SavedPlayer.clone(savedPlayer);
    }

    @Override
    public Optional<SavedPlayer> find(PlayerId id) {
        return Optional.ofNullable(contents.get(id))
                .map(SavedPlayer::clone);
    }

    @Override
    public SavedPlayer save(SavedPlayer player) {
        contents.put(player.getId(), SavedPlayer.clone(player));
        return SavedPlayer.clone(player);
    }
}

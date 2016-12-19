package com.davwards.elementals.game.players;

public class SavedPlayer extends Player {
    private final PlayerId id;

    public SavedPlayer(PlayerId id, String name, Integer experience) {
        super(name);
        this.id = id;
        this.experience = experience;
    }

    public PlayerId getId() {
        return id;
    }

    public static SavedPlayer clone(SavedPlayer savedPlayer) {
        return new SavedPlayer(
                savedPlayer.getId(),
                savedPlayer.getName(),
                savedPlayer.getExperience()
        );
    }
}

package com.davwards.elementals.game.players;

public class Player {
    protected Integer experience = 0;
    protected String name;

    public Player(String name) {
        this.name = name;
    }

    public Integer getExperience() {
        return experience;
    }

    public String getName() {
        return name;
    }

    public void addExperience(Integer points) {
        this.experience += points;
    }

    public void setName(String name) {
        this.name = name;
    }
}

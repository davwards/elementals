package com.davwards.elementals.players;

import com.davwards.elementals.players.notification.Notification;
import com.davwards.elementals.players.notification.FakeNotifier;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.models.UnsavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ResurrectPlayerInProcessTest extends ResurrectPlayerContract {
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private FakeNotifier fakeNotifier = new FakeNotifier();
    private ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, fakeNotifier);
    private FetchPlayer fetchPlayer = new FetchPlayer(playerRepository);

    @Override
    ResurrectPlayer useCase() {
        return resurrectPlayer;
    }

    @Override
    FetchPlayer fetchPlayer() {
        return fetchPlayer;
    }

    @Override
    SavedPlayer givenAnExistingPlayer(UnsavedPlayer player) {
        return playerRepository.save(player);
    }

    @Override
    void assertThatNotificationWasSent(PlayerId playerId, Notification.NotificationType type) {
        assertThat(fakeNotifier.notificationsSent(), hasItem(new Notification(playerId, type)));
    }
}

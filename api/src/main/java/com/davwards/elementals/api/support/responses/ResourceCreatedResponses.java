package com.davwards.elementals.api.support.responses;

import com.davwards.elementals.game.support.persistence.SavedEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public abstract class ResourceCreatedResponses<T extends SavedEntity> {
    protected ResourceCreatedResponses(UriComponentsBuilder uriBuilder, String resourceRoot) {
        this.uriBuilder = uriBuilder;
        this.resourceRoot = resourceRoot;
    }

    private final UriComponentsBuilder uriBuilder;
    private final String resourceRoot;

    protected URI resourceLocation(T resource) {
        return uriBuilder
                .path(resourceRoot + "/" + resource.getId())
                .build()
                .toUri();
    }
}

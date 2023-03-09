package com.cake.clockify.helloworld.model;

public record Installation(
        String addonId,
        String authToken,
        String workspaceId,
        String asUser,
        String apiUrl
) {}
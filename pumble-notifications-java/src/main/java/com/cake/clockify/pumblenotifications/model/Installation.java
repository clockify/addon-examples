package com.cake.clockify.pumblenotifications.model;

public record Installation(
        String addonId,
        String authToken,
        String workspaceId,
        String asUser,
        String apiUrl
) {}
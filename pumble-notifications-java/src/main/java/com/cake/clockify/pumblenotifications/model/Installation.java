package com.cake.clockify.pumblenotifications.model;

import java.util.List;

public record Installation(
        String addonId,
        String authToken,
        String workspaceId,
        String asUser,
        String apiUrl,
        List<Setting> settings
) {}
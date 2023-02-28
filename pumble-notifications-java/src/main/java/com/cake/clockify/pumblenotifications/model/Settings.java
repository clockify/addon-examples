package com.cake.clockify.pumblenotifications.model;

import java.util.List;

public record Settings(
        String workspaceId,
        String addonId,
        List<Setting> settings
) {
}
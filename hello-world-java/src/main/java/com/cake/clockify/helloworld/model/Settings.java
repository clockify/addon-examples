package com.cake.clockify.helloworld.model;

import java.util.List;

public record Settings(
        String workspaceId,
        String addonId,
        List<Setting> settings
) {
}
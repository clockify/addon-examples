package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.pumblenotifications.handler.WebhookHandler;
import com.cake.clockify.pumblenotifications.model.Installation;
import java.util.List;
import org.eclipse.jetty.http.HttpStatus;

public final class NotificationsAddon extends ClockifyAddon {
    private final Repository repository = new Repository();

    public NotificationsAddon(String publicUrl) {

        super(ClockifyManifest.builder()
            .key(System.getenv("ADDON_KEY"))
            .name(System.getenv("ADDON_NAME"))
            .baseUrl(publicUrl)
            .requireFreePlan()
            .scopes(List.of())
            .description(System.getenv("ADDON_DESCRIPTION"))
            .settings(ClockifySettings.builder()
                .settingsTabs(List.of(ClockifySettingsTab.builder()
                    .id("settings")
                    .name("Settings")
                    .settings(
                        List.of(ClockifySetting.builder()
                            .id("pumble-webhook")
                            .name("Pumble Webhook URL")
                            .allowEveryone()
                            .asTxt()
                            .value("{webhook url}")
                            .build())
                    ).build()))
                .build()
            )
            .build()
        );
        registerLifecycleEvents();
        registerSupportedEvents();
    }

    private void registerSupportedEvents() {
        WebhookHandler webhookHandler = new WebhookHandler(repository);

        // register the webhooks we are interested in
        registerWebhook(ClockifyWebhook.builder()
                .onNewTimeEntry()
                .path("/events/time-entry-created")
                .build(), webhookHandler);

        registerWebhook(ClockifyWebhook.builder()
                .onTimeEntryUpdated()
                .path("/events/time-entry-updated")
                .build(), webhookHandler);

        registerWebhook(ClockifyWebhook.builder()
                .onTimeEntryDeleted()
                .path("/events/time-entry-deleted")
                .build(), webhookHandler);
    }

    private void registerLifecycleEvents() {
        // this callback is called when the addon is installed in a workspace
        // notice that the auth token that this callback is provided with
        // has full access to the Clockify workspace and should not be leaked to the user
        // or to the frontend
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
            .path("/lifecycle/installed")
            .onInstalled()
            .build(), new AddonRequest() {
            @Override
            public void additionalHandling(AddonRequest request) {
                repository.persistInstallation(request.getBody(Installation.class));
                request.getResponse().setStatus(HttpStatus.OK_200);
            }
        });

        // this callback is called when the addon is uninstalled from the workspace
        // from now on, the addon will not be able to communicate with that workspace anymore
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
            .path("/lifecycle/uninstalled")
            .onDeleted()
            .build(), new AddonRequest() {
            @Override
            public void additionalHandling(AddonRequest request) {
                repository.removeInstallation(request.getBody(Installation.class));
                request.getResponse().setStatus(HttpStatus.OK_200);
            }
        });
    }
}

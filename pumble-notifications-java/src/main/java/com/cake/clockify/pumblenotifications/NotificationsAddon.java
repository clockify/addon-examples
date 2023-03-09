package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.cake.clockify.pumblenotifications.handler.WebhookHandler;
import com.cake.clockify.pumblenotifications.model.Installation;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;

public final class NotificationsAddon extends ClockifyAddon {
    private final Repository repository = new Repository();

    public NotificationsAddon(String publicUrl) {
        super(new AddonDescriptor(
                System.getenv("ADDON_KEY"),
                System.getenv("ADDON_NAME"),
                System.getenv("ADDON_DESCRIPTION"),
                publicUrl)
        );

        registerMiddlewares();
        registerSupportedEvents();
        registerLifecycleEvents();
        registerSettings();
    }

    private void registerMiddlewares() {
        // register a simple middleware that adapts the HttpRequest parameter
        // to an instance of its AddonRequest subclass
        // middlewares are executed in the order they are registered
        useMiddleware((request, chain) -> chain.apply(new AddonRequest(request)));
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
                .build(), (AddonRequest r) -> {

            repository.persistInstallation(r.getBody(Installation.class));
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });

        // this callback is called when the addon is uninstalled from the workspace
        // from now on, the addon will not be able to communicate with that workspace anymore
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/uninstalled")
                .onDeleted()
                .build(), (AddonRequest r) -> {

            repository.removeInstallation(r.getBody(Installation.class));
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });
    }

    private void registerSettings() {
        // register the settings that we need for this addon to be functional
        // currently we just require a URL where we can forward the events
        registerSettings(ClockifySettings.builder()
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
                .build());
    }
}

package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.RequestHandler;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cake.clockify.pumblenotifications.model.Installation;
import com.cake.clockify.pumblenotifications.model.Setting;
import com.cake.clockify.pumblenotifications.model.Settings;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class NotificationsAddon extends ClockifyAddon {
    private static final String HEADER_CLOCKIFY_WEBHOOK_EVENT_TYPE = "Clockify-Webhook-Event-Type";
    private static final String SETTING_PUMBLE_WEBHOOK_ID = "pumble-webhook";
    private final Repository repository = new Repository();
    private final MediaType mediaType = MediaType.parse("application/json");
    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    // create a handler which will handle all the received webhooks
    private final RequestHandler<AddonRequest> webhookHandler = r -> {
        Optional<Setting> webhookSetting = getPumbleWebhookUrl(r.getWorkspaceId());
        if (webhookSetting.isEmpty()) {
            return HttpResponse.builder().statusCode(HttpStatus.FORBIDDEN_403).build();
        }

        String eventType = r.getHeader(HEADER_CLOCKIFY_WEBHOOK_EVENT_TYPE);
        String webhookUrl = webhookSetting.get().value();
        Map<?, ?> payload = r.getBody(Map.class);

        String text = prettyGson.toJson(Map.of("event", eventType, "payload", payload));

        Request okhttpRequest = new Request.Builder()
                .post(RequestBody.create(prettyGson.toJson(Map.of("text", text)), mediaType))
                .url(webhookUrl)
                .build();

        try (Response response = new OkHttpClient().newCall(okhttpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Message could not be delivered");
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
    };

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

        // this callback is called whenever the addon settings are updated
        // the updated settings are included in the payload
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/settings-updated")
                .onSettingsUpdated()
                .build(), (AddonRequest r) -> {

            Settings body = r.getBody(Settings.class);
            repository.updateSettings(body.addonId(), body.settings());
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
                                        .id(SETTING_PUMBLE_WEBHOOK_ID)
                                        .name("Pumble Webhook URL")
                                        .asTxt()
                                        .value("{webhook url}")
                                        .build())
                        ).build()))
                .build());
    }

    private Optional<Setting> getPumbleWebhookUrl(String workspaceId) {
        // get the webhook URL from the addon settings
        Installation installation = repository.getInstallation(workspaceId);

        if (installation.settings() == null) {
            return Optional.empty();
        }

        List<Setting> settings = installation.settings().stream()
                .filter(s -> SETTING_PUMBLE_WEBHOOK_ID.equals(s.id()))
                .toList();

        if (settings.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(settings.get(0));
    }
}

package com.cake.clockify.helloworld;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.cake.clockify.helloworld.model.Installation;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


public final class HelloWorldAddon extends ClockifyAddon {
    private final Gson gson = new Gson();

    public HelloWorldAddon(String publicUrl) {
        super(new AddonDescriptor(
                System.getenv("ADDON_KEY"),
                System.getenv("ADDON_NAME"),
                System.getenv("ADDON_DESCRIPTION"),
                publicUrl)
        );
        registerLifecycleEvents();
        registerUiComponents();
        registerSettings();
    }

    private void registerLifecycleEvents() {
        // this callback is called when the addon is installed in a workspace
        // notice that the auth token that this callback is provided with
        // has full access to the Clockify workspace and should not be leaked to the user
        // or to the frontend
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/installed")
                .onInstalled()
                .build(), r -> {

            Installation payload = gson.fromJson(r.getBodyReader(), Installation.class);
            // handle the payload here
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });

        // this callback is called when the addon is uninstalled from the workspace
        // from now on, the addon will not be able to communicate with that workspace anymore
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/uninstalled")
                .onDeleted()
                .build(), r -> {

            Installation payload = gson.fromJson(r.getBodyReader(), Installation.class);
            // handle the payload here
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });
    }

    private void registerUiComponents() {
        registerComponent(ClockifyComponent.builder()
                .widget()
                .allowEveryone()
                .path("/ui")
                .build(), r -> {

            String html;
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("ui.html")) {
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                html = CharStreams.toString(reader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // return a response with content type HTML
            return HttpResponse.builder()
                    .statusCode(HttpStatus.OK_200)
                    .contentType("text/html")
                    .body(html)
                    .build();
        });
    }

    private void registerSettings() {
        // register the settings that we need for this addon to be functional
        registerSettings(ClockifySettings.builder()
                .settingsTabs(List.of(ClockifySettingsTab.builder()
                        .id("settings")
                        .name("Settings")
                        .settings(
                                List.of(ClockifySetting.builder()
                                        .id("displayed-text")
                                        .name("Displayed text")
                                        .allowEveryone()
                                        .asTxt()
                                        .value("Hello World!")
                                        .build())
                        ).build()))
                .build());
    }
}

package com.cake.clockify.helloworld;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.helloworld.model.Installation;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


public final class HelloWorldAddon extends ClockifyAddon {
    public static final ClockifySettings SETTINGS = ClockifySettings.builder()
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
            .build();
    private final Gson gson = new Gson();

    public HelloWorldAddon(String publicUrl) {
        super(ClockifyManifest.builder()
                .key(System.getenv("ADDON_KEY"))
                .name(System.getenv("ADDON_NAME"))
                .baseUrl(publicUrl)
                .requireStandardPlan()
                .scopes(List.of())
                .settings(SETTINGS)
                .description(System.getenv("ADDON_DESCRIPTION"))
                .build()
        );
        // lifecycles, components and webhooks can either be added to the manifest builder above
        // if their respective routes are already handled, or they can be registered below in order
        // for them to be served through the addon's servlet
        registerLifecycleEvents();
        registerUiComponents();
    }

    private void registerLifecycleEvents() {
        // this callback is called when the addon is installed in a workspace
        // notice that the auth token that this callback is provided with
        // has full access to the Clockify workspace and should not be leaked to the user
        // or to the frontend
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/installed")
                .onInstalled()
                .build(), (request, response) -> {

            Installation payload = gson.fromJson(request.getReader(), Installation.class);
            // handle the payload here
            response.setStatus(HttpStatus.OK_200);
        });

        // this callback is called when the addon is uninstalled from the workspace
        // from now on, the addon will not be able to communicate with that workspace anymore
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/uninstalled")
                .onDeleted()
                .build(), (request, response) -> {

            Installation payload = gson.fromJson(request.getReader(), Installation.class);
            // handle the payload here
            response.setStatus(HttpStatus.OK_200);
        });
    }

    private void registerUiComponents() {
        registerComponent(ClockifyComponent.builder()
                .widget()
                .allowEveryone()
                .path("/ui")
                .label("widget")
                .build(), (request, response) -> {

            String html;
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("ui.html")) {
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                html = CharStreams.toString(reader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            response.getWriter().write(html);
            response.setHeader("Content-Type", "text/html");
            response.setStatus(HttpStatus.OK_200);
        });
    }
}

package com.cake.clockify.helloworld;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.cake.clockify.addonsdk.shared.utils.JwtUtils;
import com.cake.clockify.helloworld.model.Installation;
import com.cake.clockify.helloworld.model.Setting;
import com.cake.clockify.helloworld.model.Settings;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public final class HelloWorldAddon extends ClockifyAddon {
    private static final String QUERY_AUTHORIZATION_PARAMETER = "auth_token";
    private static final String SETTING_DISPLAYED_TEXT_ID = "displayed-text";
    private final Repository repository = new Repository();

    private final TemplateEngine templateEngine;
    private final Gson gson = new Gson();

    public HelloWorldAddon(String publicUrl) {
        super(new AddonDescriptor(
                System.getenv("ADDON_KEY"),
                System.getenv("ADDON_NAME"),
                System.getenv("ADDON_DESCRIPTION"),
                publicUrl)
        );

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(getTemplateResolver());

        registerLifecycleEvents();
        registerUiComponents();
        registerSettings();
    }

    private ITemplateResolver getTemplateResolver() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private void registerLifecycleEvents() {
        // this callback is called when the addon is installed in a workspace
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/installed")
                .onInstalled()
                .build(), r -> {

            repository.persistInstallation(gson.fromJson(r.getBodyReader(), Installation.class));
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });

        // this callback is called when the addon is uninstalled from the workspace
        // from now on, the addon will not be able to communicate with that workspace anymore
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/uninstalled")
                .onDeleted()
                .build(), r -> {

            repository.removeInstallation(gson.fromJson(r.getBodyReader(), Installation.class));
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });

        // this callback is called whenever the addon settings are updated
        // the updated settings are included in the payload
        registerLifecycleEvent(ClockifyLifecycleEvent.builder()
                .path("/lifecycle/settings-updated")
                .onSettingsUpdated()
                .build(), r -> {

            Settings body = gson.fromJson(r.getBodyReader(), Settings.class);
            repository.updateSettings(body.addonId(), body.settings());
            return HttpResponse.builder().statusCode(HttpStatus.OK_200).build();
        });
    }

    private void registerUiComponents() {
        registerComponent(ClockifyComponent.builder()
                .widget()
                .allowEveryone()
                .path("/ui")
                .build(), r -> {

            // UI components receive a parameter containing the JWT token whenever they are rendered
            String[] parameters = r.getQuery().get(QUERY_AUTHORIZATION_PARAMETER);
            String jwt = Arrays.stream(Optional.ofNullable(parameters).orElse(new String[0]))
                    .findFirst()
                    .orElse(null);

            Optional<String> text = Optional.empty();
            if (jwt != null) {
                // note: you should always verify JWTs before considering any of the claims as valid
                // we are skipping the verification for this example
                String workspaceId = JwtUtils.parseJwtClaimsWithoutVerifying(jwt)
                        .getBody()
                        .get("workspace", String.class);
                text = getDisplayedTextSetting(workspaceId).map(Setting::value);
            }

            // initialize a Thymeleaf context, and pass in our text argument
            Context context = new Context();
            context.setVariable("text", text.orElse("Hello World!"));

            // get the URI of the template from the class loader
            URL resource = getClass().getClassLoader().getResource("templates/ui.html");

            // return a response with content type HTML and the processed template inside the body
            return HttpResponse.builder()
                    .statusCode(HttpStatus.OK_200)
                    .contentType("text/html")
                    .body(templateEngine.process(resource.getPath(), context))
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
                                        .id(SETTING_DISPLAYED_TEXT_ID)
                                        .name("Displayed text")
                                        .asTxt()
                                        .value("Hello World!")
                                        .build())
                        ).build()))
                .build());
    }

    private Optional<Setting> getDisplayedTextSetting(String workspaceId) {
        // get the displayed text from the addon settings
        Installation installation = repository.getInstallation(workspaceId);

        if (installation.settings() == null) {
            return Optional.empty();
        }

        return installation.settings().stream()
                .filter(s -> SETTING_DISPLAYED_TEXT_ID.equals(s.id()))
                .findFirst();
    }
}

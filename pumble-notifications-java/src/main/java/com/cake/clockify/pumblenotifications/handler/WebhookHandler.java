package com.cake.clockify.pumblenotifications.handler;

import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.shared.RequestHandler;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.cake.clockify.pumblenotifications.AddonRequest;
import com.cake.clockify.pumblenotifications.Repository;
import com.cake.clockify.pumblenotifications.model.Installation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.jetty.http.HttpStatus;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class WebhookHandler implements RequestHandler<AddonRequest> {
    private static final String HEADER_CLOCKIFY_WEBHOOK_EVENT_TYPE = "Clockify-Webhook-Event-Type";
    private final MediaType mediaType = MediaType.parse("application/json");

    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Repository repository;

    @Override
    public HttpResponse handle(AddonRequest r) {
        String eventType = r.getHeader(HEADER_CLOCKIFY_WEBHOOK_EVENT_TYPE);
        String webhookUrl = retrieveWebhookUrlFromSettings(r.getAddonId());

        String textPayload = prettyGson.toJson(Map.of(
                "event", eventType,
                "payload", r.getBody(Map.class))
        );

        Request okhttpRequest = new Request.Builder()
                .post(RequestBody.create(prettyGson.toJson(Map.of("text", textPayload)), mediaType))
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
    }

    @SneakyThrows
    private String retrieveWebhookUrlFromSettings(String addonId) {
        // retrieve installation details from the repository
        Installation installation = repository.getInstallation(addonId);
        Objects.requireNonNull(installation, "No installation found for addonId: " + addonId);

        // use the API url and the auth token to retrieve the settings from Clockify
        // the supplied auth token will only work when calling endpoints on the provided API URL
        String pathTemplate = "/addon/workspaces/%1$s/settings";
        String endpoint =
                installation.apiUrl() + String.format(pathTemplate, installation.workspaceId());

        Request request = new Request.Builder()
                .header("X-Addon-Token", installation.authToken())
                .url(endpoint)
                .get()
                .build();

        // execute the call through the OkHttp client
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                Objects.requireNonNull(body, "Could not retrieve settings.");

                // deserialize the response into the appropriate model class
                ClockifySettings settings =
                        prettyGson.fromJson(body.charStream(), ClockifySettings.class);

                ClockifySetting setting = settings.getTabs().get(0).getSettings()
                        .stream()
                        .filter(s -> "pumble-webhook".equals(s.getId()))
                        .findFirst()
                        .orElseThrow();

                // return the stored value
                return String.valueOf(setting.getValue());
            } else {
                throw new RuntimeException("Could not retrieve settings.");
            }
        }
    }
}

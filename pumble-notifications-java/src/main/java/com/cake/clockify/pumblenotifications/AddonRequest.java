package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.shared.RequestHandler;
import com.cake.clockify.addonsdk.shared.utils.JwtUtils;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public abstract class AddonRequest implements RequestHandler {
    private static final String HEADER_SIGNATURE = "Clockify-Signature";

    private String addonId;
    private String workspaceId;
    private String bodyJson;
    private HttpServletRequest request;
    HttpServletResponse response;

    public <T> T getBody(Class<T> clazz) {
        return new Gson().fromJson(bodyJson, clazz);
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String signature = request.getHeader(HEADER_SIGNATURE);

        if (signature != null) {
            // NOTE: we are not verifying the JWT for this example
            // it is always a good practice to ensure that every received request is authentic
            // your addon should not accept unsigned requests
            Claims claims = JwtUtils.parseJwtClaimsWithoutVerifying(signature).getBody();
            workspaceId = claims.get("workspaceId", String.class);
            addonId = claims.get("addonId", String.class);
        } else {
            workspaceId = null;
            addonId = null;
        }
        this.request = request;
        this.response = response;
        bodyJson = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        additionalHandling(this);
    }

    public abstract void additionalHandling(AddonRequest request);
}

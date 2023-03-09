package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.utils.JwtUtils;
import com.cake.clockify.addonsdk.shared.utils.Utils;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class AddonRequest extends HttpRequest {
    private static final String HEADER_SIGNATURE = "Clockify-Signature";

    private final String addonId;
    private final String workspaceId;

    @SneakyThrows
    public AddonRequest(HttpRequest request) {
        super(request.getMethod(), request.getUri(), request.getQuery(), request.getHeaders(), request.getBodyReader());
        String signature = request.getHeaders().get(HEADER_SIGNATURE);

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
    }

    public <T> T getBody(Class<T> clazz) {
        return Utils.GSON.fromJson(getBodyReader(), clazz);
    }

    public String getHeader(String header) {
        return getHeaders().get(header);
    }
}

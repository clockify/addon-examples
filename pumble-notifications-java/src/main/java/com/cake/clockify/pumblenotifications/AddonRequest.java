package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.utils.JwtUtils;
import com.cake.clockify.addonsdk.shared.utils.Utils;

public class AddonRequest extends HttpRequest {
    private static final String HEADER_SIGNATURE = "Clockify-Signature";

    private final String signature;
    private final String workspaceId;

    public AddonRequest(HttpRequest request) {
        super(request.getMethod(), request.getUri(), request.getQuery(), request.getHeaders(), request.getBodyReader());
        this.signature = request.getHeaders().get(HEADER_SIGNATURE);

        String workspaceId = "";
        try {
            // NOTE: we are not verifying the JWT for this example
            // it is always a good practice to ensure that every received request is authentic
            // your addon should not accept unsigned requests
            workspaceId = JwtUtils.parseWorkspaceIdFromJwtWithoutVerifying(signature);
        } catch (Exception ignore) {
        }

        this.workspaceId = workspaceId;
    }

    public String getSignature() {
        return signature;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public <T> T getBody(Class<T> clazz) {
        return Utils.GSON.fromJson(getBodyReader(), clazz);
    }

    public String getHeader(String header) {
        return getHeaders().get(header);
    }
}

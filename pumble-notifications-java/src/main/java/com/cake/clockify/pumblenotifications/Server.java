package com.cake.clockify.pumblenotifications;

import com.cake.clockify.addonsdk.shared.AddonServlet;
import com.cake.clockify.addonsdk.shared.EmbeddedServer;

public class Server {

    public static void main(String[] args) throws Exception {
        String publicUrl = System.getenv("PUBLIC_URL");
        int port = Integer.parseInt(System.getenv("LOCAL_PORT"));

        NotificationsAddon addon = new NotificationsAddon(publicUrl);

        // create a HttpServlet to handle the paths that the addon has defined
        AddonServlet servlet = new AddonServlet(addon);

        // start an embedded webserver serving the servlet instance
        // the servlet can also be served through other frameworks
        new EmbeddedServer(servlet).start(port);
    }
}

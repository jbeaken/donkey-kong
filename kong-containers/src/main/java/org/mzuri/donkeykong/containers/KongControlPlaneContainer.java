package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.network.KongNetwork;
import org.slf4j.Logger;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class KongControlPlaneContainer extends AbstractKongContainer {
    public KongControlPlaneContainer(KongNetwork kongNetwork, Logger logger, boolean enableDevPortalGui) {
        super(kongNetwork, logger);

        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/cert/cluster.crt", 0744), "/cert/cluster.crt");
        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/cert/cluster.key", 0744), "/cert/cluster.key");
        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/license/license.json", 0744), "/etc/kong/license.json");

        withEnv("KONG_ROLE", "control_plane");

        withEnv("KONG_ADMIN_LISTEN", "0.0.0.0:8001, 0.0.0.0:8444 ssl");

        withEnv("KONG_PROXY_ACCESS_LOG", "/dev/stdout");
        withEnv("KONG_ADMIN_ACCESS_LOG", "/dev/stdout");
        withEnv("KONG_PROXY_ERROR_LOG", "/dev/stderr");
        withEnv("KONG_ADMIN_ERROR_LOG", "/dev/stderr");
        withEnv("KONG_PLUGINS", "bundled");

        if (enableDevPortalGui) {
            withEnv("KONG_PORTAL_GUI_HOST", kongNetwork.getKongControlPortalGuiHost());
            addEnv("KONG_PORTAL", "on");
        }


        withEnv("KONG_CLUSTER_CERT", "/cert/cluster.crt");
        withEnv("KONG_CLUSTER_CERT_KEY", "/cert/cluster.key");


        withNetworkAliases(kongNetwork.getKongControlPlaneNetworkAlias());
        withExposedPorts(kongNetwork.getKongControlPlaneAdminApiPort(), kongNetwork.getKongControlPlaneManagerPort());//TODO not needed
    }

    public String getAdminApiUrl() {
        return getKongNetwork().getKongControlAdminApiUrl();
    }

    /**
     * portal cli command
     */
    public ExecResult switchOnDevPortal() throws IOException, InterruptedException {

        ArrayList<String> command = new ArrayList<String>(Arrays.asList("kong", "reload", "exit"));

       return execInContainer(command.toArray(new String[]{}));
    }
}

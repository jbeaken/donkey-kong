package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.network.KongNetwork;
import org.slf4j.Logger;
import org.testcontainers.utility.MountableFile;

public class KongDataPlaneContainer extends AbstractKongContainer {
    public KongDataPlaneContainer(KongNetwork kongNetwork, Logger logger) {
        super(kongNetwork, logger);

        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/cert/cluster.crt", 0744), "/cert/cluster.crt");
        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/cert/cluster.key", 0744), "/cert/cluster.key");
        withCopyFileToContainer(MountableFile.forClasspathResource("/kong/license/license.json", 0744), "/etc/kong/license.json");

        withEnv("KONG_ROLE", "data_plane");
        withEnv("KONG_DATABASE", "off");
        withEnv("KONG_ADMIN_LISTEN", "off");

        withEnv("KONG_PROXY_ACCESS_LOG", "/dev/stdout");
        withEnv("KONG_ADMIN_ACCESS_LOG", "/dev/stdout");
        withEnv("KONG_PROXY_ERROR_LOG", "/dev/stderr");
        withEnv("KONG_ADMIN_ERROR_LOG", "/dev/stderr");
        //Todo plugins
        withEnv("KONG_CLUSTER_CERT", "/cert/cluster.crt");
        withEnv("KONG_CLUSTER_CERT_KEY", "/cert/cluster.key");
        withEnv("KONG_CLUSTER_CONTROL_PLANE", (String) kongNetwork.getKongControlClusterUrl());
//        withEnv("KONG_CLUSTER_TELEMETRY_ENDPOINT", kongNetwork.getKongControlTelemetryEndClusterUrl())  //Todo

        withNetworkAliases(kongNetwork.getKongDataPlaneNetworkAlias());
    }

    public String getAdminApiUrl() {
        return getKongNetwork().getKongDataPlaneNetworkAlias();
    }

}

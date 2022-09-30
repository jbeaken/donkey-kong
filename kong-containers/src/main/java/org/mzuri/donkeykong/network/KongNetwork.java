package org.mzuri.donkeykong.network;

import groovy.lang.Delegate;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.Network;

public class KongNetwork {
    public KongNetwork() {
        this(Network.newNetwork());
    }

    public KongNetwork(Network network) {
        this.network = network;
        this.postgresNetworkAlias = "postgres";
        this.kongControlPlaneNetworkAlias = "kong-control-plane";
        this.kongDataPlaneNetworkAlias = "kong-data-plane";
        this.kongToolsNetworkAlias = "kong-tools";
        this.kongControlPlaneAdminApiPort = 8001;
        this.kongControlPlanePortalApiPort = 8004;
        this.kongControlPlanePortalPort = 8003;
        this.kongControlPlaneClusterPort = 8005;
        this.kongControlPlaneManagerPort = 8002;
    }

    public String getKongControlAdminApiUrl() {
        return "http://" + getKongControlPlaneNetworkAlias() + ":" + String.valueOf(getKongControlPlaneAdminApiPort());
    }

    public String getKongControlClusterUrl() {
        return getKongControlPlaneNetworkAlias() + ":" + String.valueOf(getKongControlPlaneClusterPort());
    }

    public String getKongControlPortalUrl() {
        return "http://" + String.valueOf(getKongControlPortalGuiHost());
    }

    public String getKongControlPortalGuiHost() {
        return getKongControlPlaneNetworkAlias() + ":" + String.valueOf(getKongControlPlanePortalPort());
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public String getPostgresNetworkAlias() {
        return postgresNetworkAlias;
    }

    public void setPostgresNetworkAlias(String postgresNetworkAlias) {
        this.postgresNetworkAlias = postgresNetworkAlias;
    }

    public String getKongControlPlaneNetworkAlias() {
        return kongControlPlaneNetworkAlias;
    }

    public void setKongControlPlaneNetworkAlias(String kongControlPlaneNetworkAlias) {
        this.kongControlPlaneNetworkAlias = kongControlPlaneNetworkAlias;
    }

    public String getKongDataPlaneNetworkAlias() {
        return kongDataPlaneNetworkAlias;
    }

    public void setKongDataPlaneNetworkAlias(String kongDataPlaneNetworkAlias) {
        this.kongDataPlaneNetworkAlias = kongDataPlaneNetworkAlias;
    }

    public Integer getKongControlPlaneAdminApiPort() {
        return kongControlPlaneAdminApiPort;
    }

    public void setKongControlPlaneAdminApiPort(Integer kongControlPlaneAdminApiPort) {
        this.kongControlPlaneAdminApiPort = kongControlPlaneAdminApiPort;
    }

    public Integer getKongControlPlaneManagerPort() {
        return kongControlPlaneManagerPort;
    }

    public void setKongControlPlaneManagerPort(Integer kongControlPlaneManagerPort) {
        this.kongControlPlaneManagerPort = kongControlPlaneManagerPort;
    }

    public Integer getKongControlPlanePortalApiPort() {
        return kongControlPlanePortalApiPort;
    }

    public void setKongControlPlanePortalApiPort(Integer kongControlPlanePortalApiPort) {
        this.kongControlPlanePortalApiPort = kongControlPlanePortalApiPort;
    }

    public Integer getKongControlPlaneClusterPort() {
        return kongControlPlaneClusterPort;
    }

    public void setKongControlPlaneClusterPort(Integer kongControlPlaneClusterPort) {
        this.kongControlPlaneClusterPort = kongControlPlaneClusterPort;
    }

    public Integer getKongControlPlanePortalPort() {
        return kongControlPlanePortalPort;
    }

    public void setKongControlPlanePortalPort(Integer kongControlPlanePortalPort) {
        this.kongControlPlanePortalPort = kongControlPlanePortalPort;
    }

    public String getKongToolsNetworkAlias() {
        return kongToolsNetworkAlias;
    }

    public void setKongToolsNetworkAlias(String kongToolsNetworkAlias) {
        this.kongToolsNetworkAlias = kongToolsNetworkAlias;
    }

    private Network network = Network.newNetwork();
    private String postgresNetworkAlias;
    private String kongControlPlaneNetworkAlias;
    private String kongDataPlaneNetworkAlias;
    private Integer kongControlPlaneAdminApiPort;
    private Integer kongControlPlaneManagerPort;
    private Integer kongControlPlanePortalApiPort;
    private Integer kongControlPlaneClusterPort;
    private Integer kongControlPlanePortalPort;
    private String kongToolsNetworkAlias;
}

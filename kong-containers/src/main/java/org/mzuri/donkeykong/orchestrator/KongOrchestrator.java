package org.mzuri.donkeykong.orchestrator;

import org.mzuri.donkeykong.containers.KongBootstrapDatabaseContainer;
import org.mzuri.donkeykong.containers.KongControlPlaneContainer;
import org.mzuri.donkeykong.containers.KongDataPlaneContainer;
import org.mzuri.donkeykong.containers.KongToolsContainer;
import org.mzuri.donkeykong.network.KongNetwork;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Slf4j
public class KongOrchestrator {
    public KongOrchestrator() {

        postgreSQLContainer.start();

        log.info("Waiting for postgres....");

//        postgreSQLContainer.waitUntilContainerStarted(); //TODO

        log.info("...postgres ready for connections");

        //Kong container which will bootstrap database
        KongBootstrapDatabaseContainer kongBootstrapDatabaseContainer = new KongBootstrapDatabaseContainer(kongNetwork, log);
        kongBootstrapDatabaseContainer.start();
//        kongBootstrapDatabaseContainer.waitUntilContainerStarted(); //TODO

        log.info("Boot kong control plane");
        kongControlPlaneContainer.start();
        kongControlPlaneContainer.waitingFor(Wait.forHealthcheck());
        log.info("Success at docker network url : " + getKongControlPlaneContainer().getAdminApiUrl());

        log.info("Boot kong data plane");
        kongDataPlaneContainer.start();
        kongDataPlaneContainer.waitingFor(Wait.forHealthcheck());
        log.info("Success at docker network url : " + getKongDataPlaneContainer().getAdminApiUrl());

        log.info("Boot kong tools container");
        kongToolsContainer.start();
        kongToolsContainer.waitingFor(Wait.forHealthcheck());
    }

    public void tearDown() {
        kongToolsContainer.stop();
        postgreSQLContainer.stop();
        kongControlPlaneContainer.stop();
    }

    public KongNetwork getKongNetwork() {
        return kongNetwork;
    }

    public GenericContainer getPostgreSQLContainer() {
        return postgreSQLContainer;
    }

    public KongControlPlaneContainer getKongControlPlaneContainer() {
        return kongControlPlaneContainer;
    }

    public KongDataPlaneContainer getKongDataPlaneContainer() {
        return kongDataPlaneContainer;
    }



    private KongNetwork kongNetwork = new KongNetwork();
    private GenericContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.7-alpine")
            .withUsername("kong")
            .withPassword("kong")
            .withDatabaseName("kong")
            .withNetwork(kongNetwork.getNetwork())
            .withNetworkAliases("postgres");

    private KongControlPlaneContainer kongControlPlaneContainer = new KongControlPlaneContainer(kongNetwork, log, true);
    private KongDataPlaneContainer kongDataPlaneContainer = new KongDataPlaneContainer(kongNetwork, log);

    @Getter
    private KongToolsContainer kongToolsContainer = new KongToolsContainer(kongNetwork, log);
}

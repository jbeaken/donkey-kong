package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.network.KongNetwork;
import org.slf4j.Logger;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;

public class KongBootstrapDatabaseContainer extends AbstractKongContainer {
    public KongBootstrapDatabaseContainer(KongNetwork network, Logger logger) {
        super(network, logger);

        withStartupCheckStrategy(new OneShotStartupCheckStrategy());
        withCommand("kong migrations bootstrap");
    }
}

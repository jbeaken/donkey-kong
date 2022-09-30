package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.network.KongNetwork;
import lombok.Getter;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainer<BaseContainer extends GenericContainer<BaseContainer>> extends GenericContainer<BaseContainer> {
    public AbstractContainer(DockerImageName imageName, KongNetwork kongNetwork, Logger logger) {
        super(imageName);
        this.kongNetwork = kongNetwork;
        withNetwork(kongNetwork.getNetwork());
        withLogConsumer(new Slf4jLogConsumer(logger));
    }

    public void close() {
        super.close();
    }

    @Getter
    private final KongNetwork kongNetwork;
}

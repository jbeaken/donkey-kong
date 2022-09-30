package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.network.KongNetwork;
import org.slf4j.Logger;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractKongContainer extends AbstractContainer<AbstractKongContainer> {
    public AbstractKongContainer(KongNetwork kongNetwork, Logger logger) {

        super(ENTERPRISE_IMAGE_NAME, kongNetwork, logger);

        //Database
        withEnv("KONG_DATABASE", "postgres");
        withEnv("KONG_PG_HOST", "postgres");
        withEnv("KONG_PG_PORT", "5432");
        withEnv("KONG_PG_DATABASE", "kong");
        withEnv("KONG_PG_USER", "kong");
        withEnv("KONG_PG_PASSWORD", "kong");
    }

    public static String getKONG_IMAGE() {
        return KONG_IMAGE;
    }

    private static final String KONG_IMAGE = "956698698055.dkr.ecr.us-east-2.amazonaws.com/kong";
    private static final DockerImageName ENTERPRISE_IMAGE_NAME = DockerImageName.parse(KONG_IMAGE);
}

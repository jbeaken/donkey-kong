package org.mzuri.donkeykong.containers;

import org.mzuri.donkeykong.exception.DonkeyKongException;
import org.mzuri.donkeykong.network.KongNetwork;
import groovy.json.JsonSlurper;
import org.mzuri.donkeykong.commands.*;
import org.slf4j.Logger;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.*;

public class KongToolsContainer extends AbstractContainer<KongToolsContainer> {
    public KongToolsContainer(KongNetwork kongNetwork, Logger logger) {
        super(KONG_TOOLS_IMAGE_NAME, kongNetwork, logger);

        withCommand("/bin/sh", "-c", "while true; do sleep 1000; done");

        withEnv("KC", "http://" + kongNetwork.getKongControlPlaneNetworkAlias());
        withEnv("KONG_ADMIN_URL", (String) kongNetwork.getKongControlAdminApiUrl());

        withCopyFileToContainer(MountableFile.forClasspathResource("/workspaces", 0744), "/workspaces");
    }

    /**
     * Data plane
     */
    public ExecResult executeDataplaneHttpRequest(String path, String clientId, String clientSecret) throws IOException, InterruptedException {

        List<String> command = Arrays.asList("curl", "-v", "http://kong-data-plane:8000" + path);

        if(clientId == null) throw new DonkeyKongException("Client id must not be null");
        if(clientSecret == null) throw new DonkeyKongException("Client secret must not be null");

        command.addAll(List.of("-u", clientId + ":" + clientSecret));

        return execInContainer(command.toArray(new String[]{}));
    }

    public ExecResult executeDataplaneHttpRequest(String path) throws IOException, InterruptedException {

        List<String> command = Arrays.asList("curl", "-v", "http://kong-data-plane:8000" + path);

        return execInContainer(command.toArray(new String[]{}));
    }

    public ExecResult executeDataplaneHttpRequestWithAuthorizationBearerAccessToken(String path, String accessToken) throws IOException, InterruptedException {

        List<String> command = Arrays.asList("curl", "-v", "http://kong-data-plane:8000" + path);

        command.addAll(List.of("-H", "Authorization: Bearer " + accessToken));

        return execInContainer(command.toArray(new String[]{}));
    }

    /**
     * Keycloak
     */
    public ExecResult getAccessToken(final String clientId, final String secret) throws IOException, InterruptedException {

        List<String> command = Arrays.asList("curl", "-d", "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + secret, "http://keycloak:8080/auth/realms/master/protocol/openid-connect/token");

        return execInContainer(command.toArray(new String[]{}));
    }

    /**
     * portal cli command
     */
    public ExecResult executePortalCliCommand(PortalCommand portalCommand, String workspace) throws IOException, InterruptedException {

        PortalExec portalExec = new PortalExec(portalCommand);

        List<String> command = portalExec.buildCommand(workspace);

        return execInContainer(command.toArray(new String[]{}));
    }

    /**
     * inso generate config $apiMap.specification.filename
     * Generated file is sent to stdout
     */
    public ExecResult executeInsomniaCommand(String oasFile) throws IOException, InterruptedException {

        copyFileToContainer(Transferable.of(oasFile.getBytes()), "/tmp/oas.yaml");

        List<String> command = List.of("inso", "generate", "config", "/tmp/oas.yaml");

        return execInContainer(command.toArray(new String[]{}));
    }

    /**
     * Kong dev portal gui
     */
    public ExecResult executeDevPortalHeartbeat() throws IOException, InterruptedException {

        List<String> command = List.of("curl", "-v", getKongNetwork().getKongControlPortalUrl());

        return execInContainer(command.toArray(new String[]{}));
    }

    /**
     * Kong Admin Api REST calls (via curl)
     */
    public Object executeKongAdminApiAsJson(String workspace, AdminApiCommand adminApiCommand) throws IOException, InterruptedException {
        return executeKongAdminApiAsJson(workspace, adminApiCommand, Map.of());
    }

    public Object executeKongAdminApiAsJson(AdminApiCommand adminApiCommand) throws IOException, InterruptedException {
        return executeKongAdminApiAsJson(null, adminApiCommand, Map.of());
    }

    public Object executeKongAdminApiAsJson(AdminApiExec adminApiExec) throws IOException, InterruptedException {
        ExecResult execResult = executeKongAdminApi(null, adminApiExec);
        return new JsonSlurper().parseText(execResult.getStdout());
    }

    public Object executeKongAdminApiAsJson(String workspace, AdminApiCommand adminApiCommand,Map<String, String> extraArguments) throws IOException, InterruptedException {
        ExecResult execResult = executeKongAdminApi(workspace, adminApiCommand, extraArguments);
        return new JsonSlurper().parseText(execResult.getStdout());
    }

    public ExecResult executeKongAdminApi(String workspace, AdminApiCommand adminApiCommand) throws IOException, InterruptedException {
        return executeKongAdminApi(workspace, adminApiCommand, Map.of());
    }

    public ExecResult executeKongAdminApi(String workspace, AdminApiCommand adminApiCommand, Map<String, String> extraArguments) throws IOException, InterruptedException {

        AdminApiExec adminApiExec = new AdminApiExec(adminApiCommand);

        return executeKongAdminApi(workspace, adminApiExec, extraArguments);
    }

    public ExecResult executeKongAdminApi(String workspace, AdminApiExec adminApiExec) throws IOException, InterruptedException {

        return executeKongAdminApi(workspace, adminApiExec, Map.of());
    }

    public ExecResult executeKongAdminApi(String workspace, AdminApiExec adminApiExec, Map<String, String> extraArguments) throws IOException, InterruptedException {

        String[] command = adminApiExec.buildCommand(workspace, getKongNetwork().getKongControlAdminApiUrl(), extraArguments);

        return execInContainer(command);
    }

    /**
     * Deck executions
     */
    public ExecResult executeDeck(DeckExec deckExec) throws IOException, InterruptedException {

        int index = 0;
        for(String configFile : deckExec.getConfigFiles()) {
            final String configAsString = new Yaml().dump(configFile);
            copyFileToContainer(Transferable.of(configAsString.getBytes()), "/tmp/" + index++ + ".yaml");
        }


        List<String> commands = deckExec.buildCommand(getKongNetwork().getKongControlAdminApiUrl());

        return execInContainer(commands.toArray(new String[]{}));
    }

    /**
     * upload spec to dev portal
     */
    public ExecResult executePortalSpecCurl(String workspace) throws IOException, InterruptedException {

        List<String> command = Arrays.asList("curl", getKongNetwork().getKongControlPortalUrl(), "-X", "POST", "-F", "contents=@specification-published.yaml", "-F", "path=specs/httpbin.org/oas.yaml");

        return execInContainer(command.toArray(new String[]{}));
    }

    private static final DockerImageName KONG_TOOLS_IMAGE_NAME = DockerImageName.parse("956698698055.dkr.ecr.us-east-2.amazonaws.com/kong-tools");
}

package org.mzuri.donkeykong.orchestrator

import org.mzuri.donkeykong.orchestrator.KongOrchestrator
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import spock.lang.Specification

abstract class KeycloakSpecification extends Specification {

    @Delegate
    final KongOrchestrator kongOrchestrator = new KongOrchestrator()

    KeycloakContainer keycloakContainer = new KeycloakContainer()
            .withAdminUsername("username")
            .withNetwork(kongNetwork.network)
            .withNetworkAliases("keycloak")
            .withAdminPassword("password")
            .withExposedPorts(8080)

    def setup() {
        keycloakContainer.start()
    }

    def getKeycloakAdmin() {
        Keycloak keycloakAdminClient = KeycloakBuilder.builder()
                .serverUrl(keycloakContainer.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(keycloakContainer.getAdminUsername())
                .password(keycloakContainer.getAdminPassword())
                .build();

        return keycloakAdminClient
    }
}

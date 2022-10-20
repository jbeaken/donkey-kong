package org.mzuri.donkeykong.orchestrator

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.token.AccessToken
import groovy.util.logging.Slf4j
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.ProtocolMapperRepresentation
import org.keycloak.representations.info.ServerInfoRepresentation
import spock.lang.Requires

import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.*;

@Slf4j
@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class KeycloakOrchestrationSpec extends KeycloakSpecification {


    def "Boot up all kong containers and keycloak, create client with service account"() {
        given: "We build keycloak admin"
        Keycloak keycloakAdminClient = getKeycloakAdmin()

        when: "We create client"
        ClientRepresentation clientRepresentation = new ClientRepresentation()
        clientRepresentation.setClientId("clientId")
        clientRepresentation.setSecret("secret")
        clientRepresentation.setPublicClient(false)
//        clientRepresentation.setClientAuthenticatorType(false)
        clientRepresentation.setProtocol("openid-connect")
        clientRepresentation.setServiceAccountsEnabled(true)


        and: "We add a protocol mapper"
        //{"protocol":"openid-connect","config {"multivalued":"true","id.token.claim":"true","access.token.claim":"true","userinfo.token.claim":"true","claim.name":"asdas"},"name":"asdas","protocolMapper":"oidc-usermodel-realm-role-mapper"}
        def protocolMapperRepresentation = new ProtocolMapperRepresentation()
        protocolMapperRepresentation.protocol = "openid-connect"
        protocolMapperRepresentation.name = "hello"
        protocolMapperRepresentation.protocolMapper = "oidc-hardcoded-claim-mapper"
        Map<String, String> config = Map.of("multivalued", "true", "id.token.claim", "true", "access.token.claim", "true", "userinfo.token.claim", "true", "claim.name", "hello", "claim.value","test", "jsonType.label","String")
        protocolMapperRepresentation.config = config
        clientRepresentation.setProtocolMappers(List.of(protocolMapperRepresentation))


        keycloakAdminClient.realm("master").clients().create(clientRepresentation)

        then: "We can access it"
        ClientRepresentation clientRepresentationReadOnly = keycloakAdminClient.realm("master").clients().findByClientId("clientId").get(0)
        clientRepresentationReadOnly.isServiceAccountsEnabled() == true
        clientRepresentationReadOnly.isPublicClient() == false

        and: "stuff"
        AuthorizationGrant clientGrant = new ClientCredentialsGrant()
        ClientID clientID = new ClientID("clientId")
        Secret clientSecret = new Secret("secret")
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret)

//        Scope scope = new Scope("read", "write");
        def port = keycloakContainer.getMappedPort(8080)
        URI tokenEndpoint = new URI("http://localhost:$port/auth/realms/master/protocol/openid-connect/token");

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, null);
        AccessTokenResponse accessTokenResponse = TokenResponse.parse(request.toHTTPRequest().send());
        AccessToken accessToken = accessTokenResponse.getTokens().getAccessToken()

        //nimbus jwt/jose library
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL("http://localhost:$port/auth/realms/master/protocol/openid-connect/certs"));

        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);

        jwtProcessor.setJWSKeySelector(keySelector);
        JWTClaimsSet claimsSet = jwtProcessor.process(accessToken.toString(), null);
        claimsSet.claims.size() == 18
        claimsSet.claims.get("hello").toString() == "test"

        //com.auth0 library
        DecodedJWT jwt = JWT.decode(accessToken.toString())
        def decode = Base64.decoder.decode(jwt.header)
        new String(decode, "UTF-8").startsWith("""{"alg":"RS256","typ" : "JWT","kid" : """)
    }

    def "Boot up all kong containers and keycloak, use keycloak sdk"() {
        when: "We build keycloak admin"
        Keycloak keycloakAdminClient = KeycloakBuilder.builder()
                .serverUrl(keycloakContainer.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(keycloakContainer.getAdminUsername())
                .password(keycloakContainer.getAdminPassword())
                .build();

        then: "It is valid"
        ServerInfoRepresentation serverInfo = keycloakAdminClient.serverInfo().getInfo();
        serverInfo.getSystemInfo().getVersion() == "17.0.0";
    }

    def "Boot up all kong containers and keycloak, use kcadm cli"() {
        when: "Ping keycloak control plane"
        def execResult = keycloakContainer.execInContainer("/opt/jboss/keycloak/bin/kcadm.sh",
                "config",
                "credentials",
                "--server",
                "http://localhost:8080/auth",
                "--realm",
                "master",
                "--user",
                "username",
                "--password",
                "password")

        then: "Response is valid and alive"
        execResult.stderr == "Logging into http://localhost:8080/auth as user username of realm master\n"
        execResult.exitCode == 0
    }
}

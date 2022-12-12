package org.mzuri.donkeykong.orchestrator

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import org.mzuri.donkeykong.commands.AdminApiExecBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.ProtocolMapperRepresentation
import spock.lang.Requires

@Slf4j
@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class ClientCredentialsSpec extends KeycloakSpecification {

    def "Boot up all kong containers and keycloak, create client with service account"() {
        given: "We build keycloak admin"
        Keycloak keycloakAdminClient = getKeycloakAdmin()
        AdminApiExecBuilder adminApiExecBuilder = new AdminApiExecBuilder()

        and: "We create client"
        ClientRepresentation clientRepresentation = getClientRepresentation()

        and: "We add client to master realm"
        keycloakAdminClient.realm("master").clients().create(clientRepresentation)

        when: "We create a service"
        def serviceConfig = adminApiExecBuilder.buildService("httpbin.org", "/anything", "httpbin")
        def service = kongToolsContainer.executeKongAdminApiAsJson(serviceConfig)

        and: "We create a route"
        def routeConfig = adminApiExecBuilder.buildRoute("httpbin", ["/httpbin/v1"], "httpbin")
        def route = kongToolsContainer.executeKongAdminApiAsJson(routeConfig)

        and: "We create an oidc plugin"
        def oidcConfig = adminApiExecBuilder.buildOidcPlugin(service.name)
        def oidcPlugin = kongToolsContainer.executeKongAdminApiAsJson(oidcConfig)

        and: "We wait for dataplane to sync"
        Thread.sleep(7000)

        and: "We probe dataplane api endpoint without basic authorization"
        def executeCurlCommand = kongToolsContainer.executeDataplaneHttpRequest("/httpbin/v1")
        def httpbinResponse = new JsonSlurper().parseText(executeCurlCommand.stdout)

        then: "Probe fails as basic authorization is missing"
        def port = kongControlPlaneContainer.getMappedPort(8002)
        httpbinResponse.message == "Unauthorized (no suitable authorization credentials were provided)"

        and: "We probe dataplane api endpoint with basic authorization"
        def executeCurlCommandWithBasicAuthentication = kongToolsContainer.executeDataplaneHttpRequest("/httpbin/v1", "clientId", "secret")
        def httpbinBasicAuthenticationResponse = new JsonSlurper().parseText(executeCurlCommandWithBasicAuthentication.stdout)

        then: "Response is allowed"
        httpbinBasicAuthenticationResponse.args == [:]
        httpbinBasicAuthenticationResponse.headers['Host'] == "httpbin.org"
        httpbinBasicAuthenticationResponse.headers['User-Agent'] == "curl/7.64.0"
        httpbinBasicAuthenticationResponse.headers['Authorization'] =~ /Bearer .*$/

        and: "Token contains claim"
        def authorizationHeader = httpbinBasicAuthenticationResponse.headers['Authorization']
        def matcher = authorizationHeader =~ /Bearer (?<token>.*)$/
        def test = matcher[0][0]  //why
        def token = matcher.group("token")
        DecodedJWT jwt = JWT.decode(token)
        def claims = jwt.claims
        claims.size() == 18
        claims['embeddedclaim'].asString() == 'embeddedclaimvalue'
    }

    private ClientRepresentation getClientRepresentation() {
        ClientRepresentation clientRepresentation = new ClientRepresentation()
        clientRepresentation.setClientId("clientId")
        clientRepresentation.setSecret("secret")
        clientRepresentation.setPublicClient(false)
        clientRepresentation.setProtocol("openid-connect")
        clientRepresentation.setServiceAccountsEnabled(true)

        //{"protocol":"openid-connect","config {"multivalued":"true","id.token.claim":"true","access.token.claim":"true","userinfo.token.claim":"true","claim.name":"asdas"},"name":"asdas","protocolMapper":"oidc-usermodel-realm-role-mapper"}
        def protocolMapperRepresentation = new ProtocolMapperRepresentation()
        protocolMapperRepresentation.protocol = "openid-connect"
        protocolMapperRepresentation.name = "embeddedclaim"
        protocolMapperRepresentation.protocolMapper = "oidc-hardcoded-claim-mapper"
        Map<String, String> config = Map.of("multivalued", "true", "id.token.claim", "true", "access.token.claim", "true", "userinfo.token.claim", "true", "claim.name", "embeddedclaim", "claim.value","embeddedclaimvalue", "jsonType.label","String")
        protocolMapperRepresentation.config = config
        clientRepresentation.setProtocolMappers(List.of(protocolMapperRepresentation))

        clientRepresentation
    }
}

package org.mzuri.donkeykong.executors

import org.mzuri.donkeykong.commands.AdminApiExecBuilder
import org.mzuri.donkeykong.orchestrator.KongOrchestrator
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Requires
import spock.lang.Specification

@Slf4j
@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class KongAdminApiExecutorSpec extends Specification {

    @Delegate
    final KongOrchestrator kongOrchestrator = new KongOrchestrator()

    def "create kong service"() {
        given: "We boot a exec builder"
        AdminApiExecBuilder adminApiExecBuilder = new AdminApiExecBuilder()

        when: "We create a service"
        def serviceConfig = adminApiExecBuilder.buildService("httpbin.org", "/anything", "httpbin")
        def service = kongToolsContainer.executeKongAdminApiAsJson(serviceConfig)

        and: "We create a route"
        def routeConfig = adminApiExecBuilder.buildRoute("httpbin", ["/httpbin/v1"], "httpbin")
        def route = kongToolsContainer.executeKongAdminApiAsJson(routeConfig)

        and: "We wait for dataplane to sync"
        Thread.sleep(7000)

        and: "We probe dataplane api endpoint"
        def executeCurlCommand = kongToolsContainer.executeDataplaneHttpRequest("/httpbin/v1")
        def httpbinResponse = new JsonSlurper().parseText(executeCurlCommand.stdout)

        and: "We create an oidc plugin"
        def oidcConfig = adminApiExecBuilder.buildOidcPlugin(service.name)
        def oidcPlugin = kongToolsContainer.executeKongAdminApiAsJson(oidcConfig)

        then:
        service.port == 80
        service.path == "/anything"
        service.host == "httpbin.org"
        service.name == "httpbin"

        and:
        route.protocols[0] == "http"
        route.paths[0] == "/httpbin/v1"
        route.name == "httpbin"

        and:
        httpbinResponse.args == [:]
        httpbinResponse.headers['Host'] == "httpbin.org"
        httpbinResponse.headers['User-Agent'] == "curl/7.64.0"

        and:
        oidcPlugin.name == "openid-connect"
        oidcPlugin.config.issuer == "http://keycloak:8080/auth/realms/master/.well-known/openid-configuration"
        oidcPlugin.config.scopes[0] == "openid"
        oidcPlugin.config.response_type[0] == "code"
        oidcPlugin.config.auth_methods[0] == "client_credentials"
    }

    def "create kong route"() {
        given: "We create a route"
        AdminApiExecBuilder adminApiExecBuilder = new AdminApiExecBuilder()

        def route = adminApiExecBuilder.buildRoute("httpbin.org", List.of("/anything"), "serviceName");

        def json = kongToolsContainer.executeKongAdminApiAsJson(route)

        json.port == 80
        json.path == "/anything"
        json.host == "httpbin.org"
    }
}

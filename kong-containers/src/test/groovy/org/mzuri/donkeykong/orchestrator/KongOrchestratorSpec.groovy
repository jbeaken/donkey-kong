package org.mzuri.donkeykong.orchestrator

import org.mzuri.donkeykong.orchestrator.KongOrchestrator
import org.mzuri.donkeykong.commands.DeckCommand
import groovy.util.logging.Slf4j
import org.mzuri.donkeykong.commands.DeckExec
import org.testcontainers.containers.Container
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml
import spock.lang.Requires
import spock.lang.Specification

@Slf4j
@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class KongOrchestratorSpec extends Specification {

    @Delegate
    final KongOrchestrator kongOrchestrator = new KongOrchestrator()

    def "Boot up all kong containers"() {
        when: "Ping postgres"
        def execResult = postgreSQLContainer.execInContainer("psql", "--version")

        then: "Response is valid and alive"
        execResult.stdout == "psql (PostgreSQL) 13.7\n"
        execResult.exitCode == 0

        when: "Ping kong tools"
        def execResultKongTools = kongToolsContainer.execInContainer("deck", "version")

        then: "Response is valid and alive"
        execResultKongTools.stdout ==  "decK v1.10.0 (27094f2) \n"
        execResultKongTools.exitCode == 0

        when: "Ping kong control plane"
        def execResultKongControlPlane = kongControlPlaneContainer.execInContainer("kong", "version")

        then: "Response is valid and alive"
        execResultKongControlPlane.stdout ==  "Kong Enterprise 2.7.1.1\n"
        execResultKongControlPlane.exitCode == 0

        when: "Ping kong data plane"
        def execResultKongDataPlane = kongDataPlaneContainer.execInContainer("kong", "version")

        then: "Response is valid and alive"
        execResultKongDataPlane.stdout ==  "Kong Enterprise 2.7.0.0\n"
        execResultKongDataPlane.exitCode == 0
    }

    def "execute deck"() {
        when: "We execute deck with no plugins or meta"
        def deckExec =  new DeckExec(DeckCommand.DIFF, "workspace", [ "/tmp/kong.yaml" : [:], "/tmp/meta.yaml" : [:], "/tmp/plugins.yaml" : [:] ])
        Container.ExecResult execResult = kongToolsContainer.executeDeck(deckExec)

        then: "It executes successfully"
        execResult.stdout == "Summary:\n  Created: 0\n  Updated: 0\n  Deleted: 0\n"
        execResult.exitCode == 0
    }

    def "execute insomnia"() {
        given: "We load an openapi specification"
        File openapiFile = new File(getClass().getResource("/openapi/openapi.yaml").toURI())

        when: "We execute inso generate config"
        Container.ExecResult execResult = kongToolsContainer.executeInsomniaCommand(openapiFile.text)

        and: "We load configMap"
        def configMap = new Yaml().load(execResult.stdout)

        then: "format is valid"
        configMap['_format_version'] == "1.1"

        and: "service is valid"
        def service = configMap['services'][0]
        service.host == "httpbin.org"
        service.name == "httpbin.org"

        and: "route is valid"
        def route =  service['routes'][0]
        route.name == "httpbin.org-deleteAnythingRoot"
        route.methods[0] == 'DELETE'
        route.tags[0] == "OAS3_import"
        route.tags[1] == "OAS3file_oas.yaml"
        route.paths[0] == "/anything\$"
    }

    def "execute deck with configuration sources"() {
        given: "We build a deck exec"
        DeckExec deckExec = new DeckExec(DeckCommand.DIFF)

        def kongConfig = [_format_version: "1.1",
                          services: [[name: "httpbin.org", protocol: "https",
                                      host: "httpbin.org", port: 443,
                                      path: "/httpbin", plugins: []
                                     ]]
        ]

        and: "We add sources"
        deckExec.addSource( kongConfig )

        when: "We execute deck"
        Container.ExecResult execResult = kongToolsContainer.executeDeck(deckExec)

        then: "It executes successfully"
        execResult.stdout ==
                """creating service httpbin.org
Summary:
  Created: 1
  Updated: 0
  Deleted: 0
"""
        execResult.exitCode == 2
    }
}


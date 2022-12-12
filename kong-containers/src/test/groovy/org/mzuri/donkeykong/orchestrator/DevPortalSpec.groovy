package org.mzuri.donkeykong.orchestrator

import org.mzuri.donkeykong.commands.AdminApiCommand
import org.mzuri.donkeykong.commands.PortalCommand
import org.mzuri.donkeykong.orchestrator.KongOrchestrator
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Requires
import spock.lang.Specification

@Slf4j
@Requires({ Boolean.valueOf(sys['test.include.test-containers']) })
class DevPortalSpec extends Specification {

    def workspace = "default"

    @Delegate
    final KongOrchestrator kongOrchestrator = new KongOrchestrator()

    def "Enable no auth dev portal"() {
        given: "Json portal config string is created"
        def config = [config : [portal_auth_conf: null, portal_auth: null, portal: true]]
        def configAsString = JsonOutput.toJson(config)

        and: "Curl arguments are created"
        def data = ["--data-raw" : configAsString]

        when: "Patch kong dev portal with enabled no-auth portal"
        def pathPortalExecResult = kongToolsContainer.executeKongAdminApiAsJson(workspace, AdminApiCommand.PORTAL_AUTHENTICATION, data)

        and: "Dev portal turned on"
        def switchOnExecResult = kongControlPlaneContainer.switchOnDevPortal()

//        and: "portal deploy default"
//        def portalDeployExecResult =  kongToolsContainer.executePortalCliCommand(PortalCommand.DEPLOY, workspace)

        then: "Dev portal is enabled"
        def portalConfig = kongToolsContainer.executeKongAdminApiAsJson(workspace, AdminApiCommand.PORTAL_CONFIGURATION)
        portalConfig.config.portal == true
        portalConfig.config.portal_auth == null

        and: "Dev portal gui is accessible"
        def heartbeatExecResult = kongToolsContainer.executeDevPortalHeartbeat()

        and: "html title is set correctly"
        Document doc = Jsoup.parse(heartbeatExecResult.stdout)
        def title = doc.select("html head title").first()
        title.text() == "Kong Portal - Home"
    }

    void cleanupSpec() {
//        kongOrchestrator.tearDown()
    }
}


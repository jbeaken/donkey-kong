package org.mzuri.donkeykong.commands

class AdminApiAbstractCommandSpec extends AbstractCommandSpec {

    def "instantiate AdminApiExec, produces correct command arguments"() {
        when: "AdminApiExec is instantiated"
        AdminApiExec adminApiExec = new AdminApiExec(adminApiCommand, data)

        then: "correct command received"
        def command = adminApiExec.buildCommand(null, kongControlPlaneUrl, [:])
        command == argumentList

        where: "admin api command is"
        adminApiCommand                         | data         |  argumentList
        AdminApiCommand.PORTAL_CONFIGURATION    |  [:]         | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/workspaces/default"]
        AdminApiCommand.PORTAL_AUTHENTICATION   |  [:]         | ["curl", "-X", "PATCH", "-v", "http://kong-control-plane:8001/default/workspaces/default", "-H", "content-type: application/json"]
        AdminApiCommand.WORKSPACES              |  [:]         | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/workspaces"]
        AdminApiCommand.ROUTE_LIST              |  [:]         | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/routes"]
        AdminApiCommand.PLUGINS                 |  [:]         | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/plugins"]

        AdminApiCommand.PORTAL_CONFIGURATION    |  ['a':'b']   | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/workspaces/default", "a", "b"]
        AdminApiCommand.PORTAL_AUTHENTICATION   |  ['a':'b']   | ["curl", "-X", "PATCH", "-v", "http://kong-control-plane:8001/default/workspaces/default", "a", "b", "-H", "content-type: application/json"]
        AdminApiCommand.WORKSPACES              |  ['a':'b']   | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/workspaces", "a", "b"]
        AdminApiCommand.ROUTE_LIST              |  ['a':'b']   | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/routes", "a", "b"]
        AdminApiCommand.PLUGINS                 |  ['a':'b']   | ["curl", "-X", "GET",   "-v", "http://kong-control-plane:8001/default/plugins", "a", "b"]
    }
}

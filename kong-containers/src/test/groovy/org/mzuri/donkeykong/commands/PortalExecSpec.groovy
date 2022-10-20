package org.mzuri.donkeykong.commands

class PortalExecSpec extends AbstractCommandSpec {

    def "instantiate PortalExec, get correct command arguments"() {
        when: "PortalExec is instantiated"
        PortalExec portalExec = new PortalExec(portalCommand)

        then: "correct command built"
        def command = portalExec.buildCommand("workspace")
        command == argumentList

        where: "portal command is"
        portalCommand               |  argumentList
        PortalCommand.DEPLOY        | ["portal", "deploy", "workspace"]
        PortalCommand.ENABLE        | ["portal", "enable", "workspace"]
        PortalCommand.DISABLE       | ["portal", "disable", "workspace"]
    }
}

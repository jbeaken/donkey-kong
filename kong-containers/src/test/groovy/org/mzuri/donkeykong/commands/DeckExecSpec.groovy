package org.mzuri.donkeykong.commands

class DeckExecSpec extends AbstractCommandSpec {

    def "instantiate DeckExec, get correct command arguments"() {
        when: "DeckExec is instantiated"
        DeckExec deckExec = new DeckExec(deckCommand)

        then: "correct command built"
        def command = deckExec.buildCommand(kongControlPlaneUrl)
        command == argumentList

        where: "deck command is"
        deckCommand             |  argumentList
        DeckCommand.DIFF        | ["deck", "diff", "--non-zero-exit-code", "--kong-addr", kongControlPlaneUrl]
        DeckCommand.SYNC        | ["deck", "sync", "--kong-addr", kongControlPlaneUrl]
        DeckCommand.VALIDATE    | ["deck", "validate", "--kong-addr", kongControlPlaneUrl]
        DeckCommand.DUMP        | ["deck", "dump", "--kong-addr", kongControlPlaneUrl]
    }
}

package org.mzuri.donkeykong.commands;

/**
 * config Output or change configuration of the portal on the given workspace.
 * deploy Deploy changes made locally under the given workspace upstream.
 * disable Disable the portal on the given workspace.
 * enable Enable the portal on the given workspace.
 * fetch Fetches content and themes from the given workspace.
 * init Initialize a local workspace with a default cli.conf.yaml configuration file.
 * wipe Deletes all content and themes from upstream workspace.
 */
public enum PortalCommand {
    CONFIG("config"), DEPLOY("deploy"), DISABLE("disable"), ENABLE("enable"), FETCH("fetch"), INIT("init"), WIPE("wipe");

    PortalCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private String command;
}

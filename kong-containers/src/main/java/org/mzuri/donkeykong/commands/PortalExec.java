package org.mzuri.donkeykong.commands;

import lombok.Getter;

import java.util.List;

@Getter
public class PortalExec implements Command {
    public PortalExec(PortalCommand portalCommand) {
        this.portalCommand = portalCommand;
    }

    public List<String> buildCommand(String workspace) {
        return List.of("portal", portalCommand.getCommand(), workspace);
    }

    private final PortalCommand portalCommand;
}

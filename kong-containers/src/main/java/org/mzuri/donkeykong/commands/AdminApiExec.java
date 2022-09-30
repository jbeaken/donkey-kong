package org.mzuri.donkeykong.commands;

import java.util.*;

public class AdminApiExec implements Command {

    private final AdminApiCommand adminApiCommand;
    private final Map<String, String> data;

    public AdminApiExec(AdminApiCommand adminApiCommand) {
        this(adminApiCommand, Map.of());
    }

    public AdminApiExec(AdminApiCommand adminApiCommand, Map<String, String> data) {
        this.adminApiCommand = adminApiCommand;
        this.data = data;
    }

    public String[] buildCommand(String workspace, String kongAdminApiUrl, Map<String, String> extraArguments) {

        workspace = workspace != null ? workspace : "default";

        final String path = adminApiCommand.getPath().replace("[[workspace]]", workspace);

        final String url = workspace != null ?  kongAdminApiUrl + "/" + workspace + "/" + path : kongAdminApiUrl + "/" + path;

        final List<String> command = new ArrayList<>(Arrays.asList("curl", "-X", adminApiCommand.getMethod(), "-v", url));

        addExtraArguments(extraArguments, command);

        addExtraArguments(data, command);

        addContentType(command);

        return command.toArray(new String[]{});
    }

    private void addContentType(List<String> command) {
        if (adminApiCommand.isContentTypeApplicationJson()) {
            command.add("-H");
            command.add("content-type: application/json");
        }
    }

    private static void addExtraArguments(Map<String, String> extraArguments, List<String> command) {
        extraArguments.entrySet().stream().forEach(entry -> {
            command.add(entry.getKey());
            command.add(entry.getValue());
        });
    }
}

package org.mzuri.donkeykong.commands;

import lombok.Getter;

@Getter
public enum AdminApiCommand {

    ROUTE_LIST("routes", "GET", false),
    ROUTE_CREATE("routes", "POST", true),
    SERVICE_LIST("services", "GET", false),
    SERVICE_CREATE("services", "POST", true),
    CONSUMERS("consumers", "GET", false),
    PLUGINS("plugins", "GET", false),
    PLUGIN_OIDC("routes/httpbin/plugins", "POST", true),
    WORKSPACES("workspaces", "GET", false),
    UPSTREAMS("upstreams", "GET", false),

    PORTAL_AUTHENTICATION("workspaces/[[workspace]]", "PATCH", true),
    PORTAL_CONFIGURATION("workspaces/[[workspace]]", "GET", false);

    private final String path;
    private final String method;
    private final boolean isContentTypeApplicationJson;

    AdminApiCommand(String path, String method, boolean isContentTypeApplicationJson) {
        this.path = path;
        this.method = method;
        this.isContentTypeApplicationJson = isContentTypeApplicationJson;
    }
}

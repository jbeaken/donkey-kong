package org.mzuri.donkeykong.resources;

import lombok.Data;

import java.util.List;

@Data
public class OIDCPlugin {

    private final String name = "openid-connect";
    private final Service service;

    private final OIDCConfig config;

    public OIDCPlugin(Service service, String issuer, boolean displayErrors, List<AuthMethod> authMethods) {
        this.service = service;
        this.config = new OIDCConfig(issuer, displayErrors, authMethods);
    }
}


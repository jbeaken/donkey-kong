package org.mzuri.donkeykong.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OIDCConfig {

    private final String issuer;

    @JsonProperty("display_errors") private final Boolean displayErrors;
    @JsonProperty("auth_methods") private final List<AuthMethod> authMethods;
    @JsonProperty("client_id") private List<String> clientIds;
    @JsonProperty("client_secret") private List<String> clientSecrets;
}

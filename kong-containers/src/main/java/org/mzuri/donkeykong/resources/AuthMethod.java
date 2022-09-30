package org.mzuri.donkeykong.resources;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum AuthMethod {

    CLIENT_CREDENTIALS("client_credentials"), INTROSPECTION("introspection");

    @Getter
    @JsonValue
    private final String name;

    AuthMethod(String name) {
        this.name = name;
    }
}

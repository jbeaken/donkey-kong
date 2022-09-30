package org.mzuri.donkeykong.resources;

import lombok.Data;

@Data
public class Service extends KongResource {

    private final String host;
    private final String path;
    private final Integer port;

    public Service(){
        this(null);
    }

    public Service(String name) {
        this(name, null, null, null);
    }

    public Service(String name, String host, String path, Integer port) {
        super(name);
        this.host = host;
        this.path = path;
        this.port = port;
    }
}

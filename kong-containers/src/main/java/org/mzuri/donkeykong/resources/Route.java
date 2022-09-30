package org.mzuri.donkeykong.resources;

import lombok.Data;

import java.util.List;

@Data
public class Route {

    private final String name;
    private final Service service;
    private final List<String> paths;
    private final List<String> protocols;

    public Route(String name, Service service, List<String> paths) {
        this(name, service, paths, List.of("http"));
    }

    public Route(String name, Service service, List<String> paths, List<String> protocols) {
        this.name = name;
        this.service = service;
        this.paths = paths;
        this.protocols = protocols;
    }
}

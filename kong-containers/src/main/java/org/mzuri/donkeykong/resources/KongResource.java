package org.mzuri.donkeykong.resources;

import lombok.Data;

import java.io.Serializable;

@Data
public class KongResource implements Serializable {

    private final String name;

    public KongResource() {
        this(null);
    }

    public KongResource(String name) {
        this.name = name;
    }
}

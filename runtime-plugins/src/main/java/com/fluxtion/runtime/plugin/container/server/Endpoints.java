/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

/**
 *
 * @author greg
 */
public enum Endpoints {
    TRACER("trace"),
    EVENT_LOGGER("eventlogger"),
    NODE_LIST("nodelist"),
    GRAPHML("graphml"),
    GRAPH_PNG("graphimage"),
    ONEVENT("onevent"),
    DASHBOARD("dashboard"),
    ;

    public String endPoint() {
        return endpoint;
    }

    public String url(String root) {
        return root + endpoint;
    }

    private final String endpoint;

    private Endpoints(String endpoint) {
        this.endpoint = "/" + endpoint;
    }
}

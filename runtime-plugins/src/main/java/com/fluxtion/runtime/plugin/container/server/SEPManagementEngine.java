/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

/**
 *
 * @author greg
 */
public class SEPManagementEngine {

    private Map<String, EventHandler> handlerMap;
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

    public SEPManagementEngine() {
        System.out.println("SEPManagementEngine::cstrctr");
        handlerMap = new HashMap<>();
    }

    public void init() {
        path("/:sep_processor", () -> {
            post("/trace", this::traceField);

        });
    }

    public void shutDown() {
        stop();
    }

    public void registerSep(EventHandler sep, String id) {
        handlerMap.put(id, sep);
    }

    private EventHandler getSep(Request req) {
        return handlerMap.getOrDefault(req.params(":sep_processor"), EventHandler.NULL_EVENTHANDLER);
    }

    public Object traceField(Request req, Response res) throws Exception {
        EventHandler sep = getSep(req);
        String traceRequest = req.body();
        TracerConfigEvent traceConfigEvent = jacksonObjectMapper.readValue(traceRequest, TracerConfigEvent.class);
        sep.onEvent(traceConfigEvent);
        return "trace set";
    }

    public static Object getSepParameter(Request req, Response res) throws Exception {
        String sepName = req.params(":name");
        System.out.printf("getting props for[%s]%n", sepName);
        return "property value";
    }

    public static Object setSepParameter(Request req, Response res) throws Exception {
        String sepName = req.params(":name");
        System.out.println("body:" + req.body());
        System.out.println("params:" + req.params());
        System.out.println("queryParams:" + req.queryParams());
        System.out.printf("setting props via queryParams for[%s] - ", sepName);
        System.out.printf("[name:%s]%n", req.queryParams("name"));
//        System.out.println("request.queryParams(\"name\"):" + req.queryParams("name"));
        return "property set";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.EVENT_LOGGER;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.TRACER;
import com.fluxtion.runtime.plugin.logging.EventLogConfig;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

/**
 * A container for managing SEP via REST calls. SEP's are registered with a
 * unique name {@link registerSep}. After registration clients can manage a SEP
 * by invoking methods on the SEPManagementEngine.
 *
 * A SEPManagementEngine is started by calling {@link init} with a port to bind
 * to. The default port is 4567. A Java client library is provided that
 * simplifies the integration with Java clients.
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

    /**
     * Initialises a the SEPManagementEngine and binds to default port 4567.
     */
    public void init() {
        init(4567);
    }


    /**
     * Initialises a the SEPManagementEngine and binds to user specified port.
     * @param port
     */
    public void init(int port) {
        stop();
        port(port);
        path("/:sep_processor", () -> {
            post(TRACER.endPoint(), this::traceField);
            post(EVENT_LOGGER.endPoint(), this::configureEventLogger);
        });
    }

    /**
     * Stops the http server.
     */
    public void shutDown() {
        stop();
    }

    /**
     * Registers a SEP with this SEPManagementEngine. The supplied identifier
     * must be unique or it will overwrite any existing 
     * @param req
     * @return 
     */
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
    
    public Object configureEventLogger(Request req, Response res) throws Exception {
        EventHandler sep = getSep(req);
        String traceRequest = req.body();
        EventLogConfig traceConfigEvent = jacksonObjectMapper.readValue(traceRequest, EventLogConfig.class);
        sep.onEvent(traceConfigEvent);
        return "event log configured";
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.EVENT_LOGGER;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.NODE_LIST;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.TRACER;
import com.fluxtion.runtime.plugin.logging.EventLogConfig;
import com.fluxtion.runtime.plugin.tracing.Tracer;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.reflect.FieldUtils;
import spark.Request;
import spark.Response;
import spark.Service;

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
    private Service spark;

    public SEPManagementEngine() {
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
     *
     * @param port
     */
    public void init(int port) {
//        stop();
        spark = Service.ignite();
        spark.port(port);
        spark.path("/:sep_processor", () -> {
            spark.post(TRACER.endPoint(), this::traceField);
            spark.post(EVENT_LOGGER.endPoint(), this::configureEventLogger);
            spark.post(NODE_LIST.endPoint(), this::getNodeList);
        });
        spark.awaitInitialization();
    }

    /**
     * Stops the http server.
     */
    public void shutDown() {
        spark.stop();
        spark.awaitInitialization();
    }

    /**
     * Registers a SEP with this SEPManagementEngine. The supplied identifier
     * must be unique or it will overwrite any existing
     *
     * @param req
     * @return
     */
    public void registerSep(EventHandler sep, String id) {
        handlerMap.put(id, sep);
    }

    private EventHandler getSep(Request req) {
        return handlerMap.getOrDefault(req.params(":sep_processor"), EventHandler.NULL_EVENTHANDLER);
    }

    /**
     * configures tracing of a field in a node
     * @param req
     * @param res
     * @return
     * @throws Exception 
     */
    public Object traceField(Request req, Response res) throws Exception {
        EventHandler sep = getSep(req);
        String traceRequest = req.body();
        TracerConfigEvent traceConfigEvent = jacksonObjectMapper.readValue(traceRequest, TracerConfigEvent.class);
        sep.onEvent(traceConfigEvent);
        return "trace set";
    }

    /**
     * configures the event logger auditor
     * @param req
     * @param res
     * @return
     * @throws Exception 
     */
    public Object configureEventLogger(Request req, Response res) throws Exception {
        EventHandler sep = getSep(req);
        String traceRequest = req.body();
        EventLogConfig traceConfigEvent = jacksonObjectMapper.readValue(traceRequest, EventLogConfig.class);
        sep.onEvent(traceConfigEvent);
        return "event log configured";
    }

    public Object getNodeList(Request req, Response res) throws Exception {
        EventHandler sep = getSep(req);
        Object retValue = "no fields found";
//        res.header(traceRequest, traceRequest);
        Optional<Field> tracerField = FieldUtils.getAllFieldsList(sep.getClass()).stream()
                .filter(f -> f.getType().equals(Tracer.class))
                .findFirst();
        if (tracerField.isPresent()) {
            Field f = tracerField.get();
            try {
                Tracer tracer = (Tracer) f.get(sep);
                retValue = jacksonObjectMapper.writeValueAsString(tracer.getNodeDescription());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(SEPManagementEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retValue;
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

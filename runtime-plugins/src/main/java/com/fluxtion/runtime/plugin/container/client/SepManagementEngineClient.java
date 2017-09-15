/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fluxtion.runtime.plugin.container.SerialisedEvent;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.EVENT_LOGGER;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.GRAPHML;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.NODE_LIST;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.TRACER;
import static com.fluxtion.runtime.plugin.container.server.Endpoints.ONEVENT;
import com.fluxtion.runtime.plugin.logging.EventLogConfig;
import com.fluxtion.runtime.plugin.reflection.NodeDescription;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import static com.mashape.unirest.http.Unirest.post;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author greg
 */
public class SepManagementEngineClient {

    private final String root_url;
    private final String sep_url;
    private final String sepName;
    
    static {
        initialise();
    }

    public SepManagementEngineClient(String root_url, String sepName) {
        this.root_url = root_url;
        this.sepName = sepName;
        this.sep_url = root_url + "/" + sepName; 
    }

    public void shutDown() throws IOException{
        Unirest.shutdown();
    }

    public HttpResponse<String> onEvent(SerialisedEvent serialisedEvent) throws UnirestException {
        return post(ONEVENT.url(sep_url)).body(serialisedEvent).asString();
    }

    public HttpResponse<String> setTrace(TracerConfigEvent traceEvent) throws UnirestException {
        return post(TRACER.url(sep_url)).body(traceEvent).asString();
    }

    public HttpResponse<String> configureEventLogger(EventLogConfig eventLoggerCfg) throws UnirestException {
        return post(EVENT_LOGGER.url(sep_url)).body(eventLoggerCfg).asString();
    }
    
    public  List<NodeDescription> getNodeDescriptions() throws UnirestException{
        HttpResponse<NodeDescription[]> asObject = post(NODE_LIST.url(sep_url)).asObject(NodeDescription[].class);
        return Arrays.asList(asObject.getBody());
    }
    
    public HttpResponse<String> getGraphMl(String sepName) throws UnirestException {
        return post(GRAPHML.url(sep_url)).asString();
    }
    
    private static void initialise() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;

/**
 *
 * @author greg
 */
public class SepManagementEngineClient {

    private String root_url;
    private String sep_url;
    private String sepName;
    
    static {
        initialise();
    }

    public SepManagementEngineClient(String root_url, String sepName) {
        this.root_url = root_url;
        this.sepName = sepName;
        this.sep_url = root_url + "/" + sepName; 
    }

    public static void main(String[] args) throws UnirestException, IOException {
        SepManagementEngineClient client = new SepManagementEngineClient("http://localhost:4567", "SEP_PROCESSOR_1");
        HttpResponse<String> resp = client.setTrace(new TracerConfigEvent("boiler", "burnerStatus", true, false));
        System.out.println(resp.getBody());
        Unirest.shutdown();
    }

    public HttpResponse<String> setTrace(TracerConfigEvent traceEvent) throws UnirestException {
        return Unirest.post(sep_url + "/trace")
                .body(traceEvent).asString();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

import com.fluxtion.learning.utils.monitoring.cooling.TemperatureEvent;
import com.fluxtion.learning.utils.monitoring.cooling.generated.RackCoolingSystem;
import com.fluxtion.runtime.plugin.container.client.SepManagementEngineClient;
import com.fluxtion.runtime.plugin.logging.EventLogConfig;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class ContainerTest {

    private static final String server1 = "server1";
    private static final String server2 = "server2";
    private static final String svrNy = "svrNy";
    private static final String svrLD = "svrLD";
    private static final String svrTky = "svrTky";
    private static final String external = "external";

    @Test
    public void traceProperty() throws UnirestException {
        SEPManagementEngine container = new SEPManagementEngine();
        container.init();
        RackCoolingSystem rackCooler = new RackCoolingSystem();
        rackCooler.init();
        rackCooler.logger.setLogSink(System.out::println);

        //to do add a property tracer to validate testing
        rackCooler.propertyTracer.addConsolePublisher();
        container.registerSep(rackCooler, "rackCooler");

        SepManagementEngineClient client = new SepManagementEngineClient("http://localhost:4567", "rackCooler");
//        client.setTrace(new TracerConfigEvent(server1, "temperature", true, false));
//        client.setTrace(new TracerConfigEvent(server1, "temperatureBreach", true, false));
//        client.setTrace(new TracerConfigEvent(server2, "temperature", true, false));
//        client.setTrace(new TracerConfigEvent(server2, "temperatureBreach", true, false));
//        client.setTrace(new TracerConfigEvent(svrNy, "temperature", true, false));
//        client.setTrace(new TracerConfigEvent(svrNy, "temperatureBreach", true, false));
        client.setTrace(new TracerConfigEvent("rackMonitor", "countWarning", true, false));
        client.setTrace(new TracerConfigEvent("rackMonitor", "percentWarning", true, false));
        client.setTrace(new TracerConfigEvent("coolingContol", "percentageWaterCooling", true, false));
        client.setTrace(new TracerConfigEvent("coolingContol", "percentageAirCooling", true, false));
        client.setTrace(new TracerConfigEvent("coolingContol", "airTemperature", true, false));

        //send some temperatures
        rackCooler.handleEvent(new TemperatureEvent(external, 25));
        rackCooler.handleEvent(new TemperatureEvent(server1, 30));
        rackCooler.handleEvent(new TemperatureEvent(server1, 39));
        rackCooler.handleEvent(new TemperatureEvent(server1, 45));
        rackCooler.handleEvent(new TemperatureEvent(server1, 49));
        rackCooler.handleEvent(new TemperatureEvent(server2, 47));
        rackCooler.handleEvent(new TemperatureEvent(svrNy, 56));
        rackCooler.handleEvent(new TemperatureEvent(external, 32));
        //kill EventLogger
        client.configureEventLogger(new EventLogConfig(EventLogConfig.LogLevel.NONE));
        client.configureEventLogger(new EventLogConfig("coolingContol", null, EventLogConfig.LogLevel.TRACE));
        rackCooler.handleEvent(new TemperatureEvent(server1, 44));
        rackCooler.handleEvent(new TemperatureEvent(server2, 40));
        rackCooler.handleEvent(new TemperatureEvent(external, 25));
        rackCooler.handleEvent(new TemperatureEvent(svrNy, 41));

    }

    private void print(HttpResponse<String> setTrace) {
        System.out.println(setTrace.getBody());
    }
}

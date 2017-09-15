/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

import com.fluxtion.learning.utils.monitoring.cooling.TemperatureEvent;
import com.fluxtion.learning.utils.monitoring.cooling.generated.RackCoolingSystem;
import com.fluxtion.runtime.plugin.container.client.SepManagementEngineClient;
import com.fluxtion.runtime.plugin.executor.AsyncEventHandler;
import com.fluxtion.runtime.plugin.executor.SingleThreadedAsyncEventHandler;
import com.fluxtion.runtime.plugin.logging.EventLogConfig;
import com.fluxtion.runtime.plugin.logging.YamlLogRecordListener;
import com.fluxtion.runtime.plugin.reflection.NodeDescription;
import com.fluxtion.runtime.plugin.tracing.TracerConfigEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
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
    private SEPManagementEngine container;
    private static LongAdder count = new LongAdder();

    static {
        count.add(5876);
    }
    private RackCoolingSystem rackCooler;
    private AsyncEventHandler managedCooler;
    private SepManagementEngineClient client;

    @Before
    public void initSpark() {
        container = new SEPManagementEngine();
        container.init(count.intValue());
        rackCooler = new RackCoolingSystem();
        rackCooler.init();
        managedCooler = new SingleThreadedAsyncEventHandler(rackCooler);
        container.registerSep(managedCooler, RACK_COOLER);
        client = new SepManagementEngineClient("http://localhost:" + count.intValue(), "rackCooler");
    }
    private static final String RACK_COOLER = "rackCooler";

    @After
    public void stopSpark() {
        count.increment();
        container.shutDown();
    }

    @Test
    public void testEventLogControl() throws UnirestException {
        final YamlLogRecordListener logListener = new YamlLogRecordListener();
        rackCooler.logger.setLogSink(logListener);
        //send an temperature events and count logs
        rackCooler.handleEvent(new TemperatureEvent(external, 25));
        rackCooler.handleEvent(new TemperatureEvent(server1, 30));
        rackCooler.handleEvent(new TemperatureEvent(server1, 49));
        rackCooler.handleEvent(new TemperatureEvent(external, 32));
        assertEquals(4, logListener.getEventList().size());
        //remove logging
        client.configureEventLogger(new EventLogConfig(EventLogConfig.LogLevel.NONE));
        logListener.getEventList().clear();
        //send an temperature events and count logs
        rackCooler.handleEvent(new TemperatureEvent(external, 25));
        rackCooler.handleEvent(new TemperatureEvent(server1, 30));
        rackCooler.handleEvent(new TemperatureEvent(server1, 49));
        rackCooler.handleEvent(new TemperatureEvent(external, 32));
        assertEquals(0, logListener.getEventList().size());
    }

    @Test
    public void testEventLogControlAsynch() throws UnirestException {
        final YamlLogRecordListener logListener = new YamlLogRecordListener();
        rackCooler.logger.setLogSink(logListener);
        //send an temperature events and count logs
        managedCooler.onEvent(new TemperatureEvent(external, 25));
        managedCooler.onEvent(new TemperatureEvent(server1, 30));
        managedCooler.onEvent(new TemperatureEvent(server1, 49));
        managedCooler.onEvent(new TemperatureEvent(external, 32));
        assertEquals(4, logListener.getEventList().size());
        //remove logging
        client.configureEventLogger(new EventLogConfig(EventLogConfig.LogLevel.NONE));
        logListener.getEventList().clear();
        //send an temperature events and count logs
        managedCooler.onEvent(new TemperatureEvent(external, 25));
        managedCooler.onEvent(new TemperatureEvent(server1, 30));
        managedCooler.onEvent(new TemperatureEvent(server1, 49));
        managedCooler.onEvent(new TemperatureEvent(external, 32));
        assertEquals(0, logListener.getEventList().size());
    }

    @Test
    public void controlTracer() throws UnirestException {
        client.setTrace(new TracerConfigEvent(server1, "temperature", true, false));
        client.setTrace(new TracerConfigEvent(server1, "temperatureBreach", true, false));
        client.setTrace(new TracerConfigEvent(server2, "temperature", true, false));
        client.setTrace(new TracerConfigEvent(server2, "temperatureBreach", true, false));
        client.setTrace(new TracerConfigEvent(svrNy, "temperature", true, false));
        client.setTrace(new TracerConfigEvent(svrNy, "temperatureBreach", true, false));
        //send an temperature events and count logs
        LongAdder accumulator = new LongAdder();
        rackCooler.propertyTracer.addListener((r) -> {
            accumulator.increment();
        });
        accumulator.reset();
        rackCooler.handleEvent(new TemperatureEvent(external, 25));
        assertEquals(6, accumulator.intValue());

        accumulator.reset();
        rackCooler.handleEvent(new TemperatureEvent(server1, 30));
        assertEquals(6, accumulator.intValue());

        client.setTrace(new TracerConfigEvent(server1, "temperatureBreach", false));
        client.setTrace(new TracerConfigEvent(server2, "temperature", false));
        client.setTrace(new TracerConfigEvent(server2, "temperatureBreach", false));
        client.setTrace(new TracerConfigEvent(svrNy, "temperature", false));
        client.setTrace(new TracerConfigEvent(svrNy, "temperatureBreach", false));

        accumulator.reset();
        rackCooler.handleEvent(new TemperatureEvent(server1, 49));
        rackCooler.handleEvent(new TemperatureEvent(external, 32));
        assertEquals(2, accumulator.intValue());
    }

    @Test
    public void readNodeDescriptions() throws UnirestException{
        List<NodeDescription> nodeDescriptions = client.getNodeDescriptions();
        assertEquals(7, nodeDescriptions.size());
        Set<String> actual = nodeDescriptions.stream().map(n -> n.getName()).collect(Collectors.toSet());
        Set<String> expected = new HashSet(Arrays.asList("coolingContol", "rackMonitor", "server1", "server2","svrLD","svrNy", "svrTky"));
        assertEquals(expected, actual);
    }
    
    @Test
    public void testEventFileLoad() throws FileNotFoundException {
        FileReader reader = new FileReader(new File("src/test/resources/eventlogs/log_test_1.yml"));
        YamlLogRecordListener eventMarshaller = new YamlLogRecordListener();
        eventMarshaller.loadFromFile(reader);
        assertEquals(2, eventMarshaller.getEventList().size());
    }
    
    @Test
    public void testGetGraphMl() throws UnirestException{
        HttpResponse<String> nodeDescriptions = client.getGraphMl(RACK_COOLER);
    }
}

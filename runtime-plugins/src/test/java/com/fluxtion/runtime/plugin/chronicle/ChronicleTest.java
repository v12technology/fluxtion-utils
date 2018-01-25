/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.chronicle;

import com.fluxtion.runtime.plugin.events.TimingPulseEvent;
import com.fluxtion.runtime.plugin.eventsink.ChronicleEventSink;
import com.fluxtion.runtime.plugin.eventsource.ChronicleEventSource;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gregp
 */
public class ChronicleTest {
    
    private static final String path = "target/generated-test-sources/";
    
    @Test
    public void testChronicleReadWrite(){
        ChronicleEventSink sink = new ChronicleEventSink(path + "test1");
        ChronicleEventSource source = new ChronicleEventSource(path + "test1");
        sink.onEvent(new TimingPulseEvent(100));
        TimingPulseEvent event = (TimingPulseEvent) source.read();
        assertThat(event.currentTimeMillis, is(100L));
    }


}

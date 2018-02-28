/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.eventsource;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.plugin.executor.EventSource;
import java.io.File;
import java.io.IOException;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reads 
 * @author gregp
 */
public class ChronicleEventSource implements EventSource {

    private final File queuePath;
    private static final Logger LOGGER = LogManager.getFormatterLogger(ChronicleEventSource.class);
    private final SingleChronicleQueue queue;
    private Event event;
    private final MethodReader methodReader;

    public ChronicleEventSource(String filePath) {
        this(filePath, true);
    }
    
    public ChronicleEventSource(String filePath, boolean toEnd) {
        queuePath = new File(filePath);
        if(!queuePath.exists()){
            queuePath.mkdirs();
        }
        queue = SingleChronicleQueueBuilder.binary(queuePath).build();
        ExcerptTailer tailer = toEnd?queue.createTailer().toEnd():queue.createTailer();
        methodReader = tailer.methodReader((EventHandler<Event>) (Event e) -> {
            event = e;
        });
        try {
            LOGGER.info("storing queue at:" + queuePath.getCanonicalPath());
        } catch (IOException ex) {
            LOGGER.error("could not read queue path", ex);
        }
    }

    @Override
    public Event read() {
        event = null;
        methodReader.readOne();
        return event;
    }

}

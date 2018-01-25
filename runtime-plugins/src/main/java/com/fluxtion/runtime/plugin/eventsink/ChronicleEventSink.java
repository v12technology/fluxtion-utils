/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.eventsink;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.plugin.events.ChronicleMarshallableEvent;
import java.io.File;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

/**
 *
 * @author gregp
 */
public class ChronicleEventSink implements EventHandler<ChronicleMarshallableEvent> {

    private EventHandler chronicleSink;

    public ChronicleEventSink(String filePath) {
        File queuePath = new File(filePath);
        if(!queuePath.exists()){
            queuePath.mkdirs();
        }
        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build();
        chronicleSink = queue.acquireAppender().methodWriter(EventHandler.class);
    }

    @Override
    public void onEvent(ChronicleMarshallableEvent e) {
        chronicleSink.onEvent(e);
    }

}

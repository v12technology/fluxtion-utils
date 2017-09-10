/*
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Limited
 */
public class ExecutorTest {

    @Test(expected = IllegalStateException.class)
    public void startStopDefault() {
        System.out.println("startStopDefault");
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "sample");
        sepExecutor.shutDown();
        sepExecutor.submit(() -> null);
    }

    @Test(expected = IllegalStateException.class)
    public void startStopBusy() {
        System.out.println("startStopBusy");
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "sample");
        sepExecutor.busySpin();
        sepExecutor.shutDown();
        sepExecutor.submit(() -> null);
    }

    @Test(expected = IllegalStateException.class)
    public void startStopLongSleep() {
        System.out.println("startStopLongSleep");
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "sample");
        sepExecutor.sleep(780_000_000);
        sepExecutor.shutDown();
        sepExecutor.submit(() -> null);
    }

    @Test(expected = IllegalStateException.class)
    public void startStopWaitOnTaskQueue() {
        System.out.println("startStopWaitOnTaskQueue");
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "sample");
        sepExecutor.waitOnTaskQueue();
        sepExecutor.shutDown();
        sepExecutor.submit(() -> null);
    }

    @Test
    public void singleThreadedAccessEventSource() throws InterruptedException, ExecutionException {
        System.out.println("singleThreadedAccessEventSource");
        String[] ans = new String[1];
        SepExecutor sepExecutor = new SepExecutor(e -> {
            ans[0] = Thread.currentThread().getName();
        }, "sample");
        MyEventSource source = new MyEventSource();
        sepExecutor.registerEventSource(source, "sampleSource");
        sepExecutor.busySpin();
        Assert.assertNull(ans[0]);
        source.createEventFlag.set(true);
        sepExecutor.shutDown();
        Assert.assertNotNull(ans[0]);
        Assert.assertEquals("sample", ans[0]);
    }

    @Test
    public void singleThreadedAccessTask() throws InterruptedException, ExecutionException {
        System.out.println("singleThreadedAccessTask");
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "sample");
        sepExecutor.waitOnTaskQueue();
        String threadName = sepExecutor.submit(() -> Thread.currentThread().getName()).get();
        sepExecutor.shutDown();
        Assert.assertEquals("sample", threadName);
    }

    @Test
    public void waitStrategy() throws InterruptedException, ExecutionException {
        System.out.println("waitStrategy");
        String[] ans = new String[1];
        SepExecutor sepExecutor = new SepExecutor(e -> {
            ans[0] = Thread.currentThread().getName();
        }, "sample");
        sepExecutor.waitOnTaskQueue();
        MyEventSource source = new MyEventSource();
        sepExecutor.registerEventSource(source, "sampleSource");
        Assert.assertNull(ans[0]);
        source.createEventFlag.set(true);
        sepExecutor.submit(() -> null).get();
        Assert.assertNotNull(ans[0]);
        Assert.assertEquals("sample", ans[0]);
        sepExecutor.shutDown();
    }

    private static class MyEventSource implements EventSource {

        AtomicBoolean createEventFlag = new AtomicBoolean(false);

        @Override
        public Event read() {
            if (createEventFlag.getAndSet(false)) {
                return new Event() {
                };
            } else {
                return null;
            }
        }

    }

}

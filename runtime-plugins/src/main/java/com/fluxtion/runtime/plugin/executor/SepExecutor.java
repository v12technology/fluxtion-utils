/* 
 * Copyright (C) 2017 V12 Technology Limited (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An executor for the
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SepExecutor {
    
    private EventHandler targetSep;
    private final String name;
    private BlockingQueue<FutureTask> queue = new ArrayBlockingQueue<>(10);
    private AtomicBoolean run = new AtomicBoolean(true);
    
    private final AtomicLong sleepMicros;
    private final Logger loggerRequests;
    private final Logger loggerTasks;
    private EventSourceDecorator eventSource;
    
    public SepExecutor(EventHandler targetSep, String name) {
        loggerRequests = LoggerFactory.getLogger("SepExecutor-requesthandler-" + name);
        loggerTasks = LoggerFactory.getLogger("SepExecutor-TaskProcessor-" + name);
        this.targetSep = targetSep;
        this.name = name;
        sleepMicros = new AtomicLong(100 * 1000);
        start();
    }
    
    public void registerEventSource(EventSource eventSource) {
        this.eventSource = new EventSourceDecorator(eventSource);
    }
    
    public void shutDown() {
        run.lazySet(false);
    }
    
    public void setSleepMicros(long sleepMicros) {
        sleepMicros = sleepMicros < 0 ? 0 : sleepMicros;
        this.sleepMicros.lazySet(sleepMicros);
    }
    
    public <T> Future<T> submit(Callable<T> task) {
        loggerRequests.info("submitting task");
        FutureTask<T> ft = new FutureTask(task);
        queue.add(ft);
        return ft;
    }
    
    private void start() {
        loggerTasks.info("starting");
        Thread t = new Thread("fred") {
            @Override
            public void run() {
                while (run.get()) {
                    try {
                        //process commands
                        loggerTasks.info("waiting to take task");
                        FutureTask future = queue.take();
                        loggerTasks.info("processing task");
                        future.run();
                        //test for read
                        eventSource.run();
                        LockSupport.parkNanos(sleepMicros.get() * 1000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(SepExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        t.start();
    }

    /**
     *
     */
    private class EventSourceDecorator implements Runnable {
        
        EventSource eventSource;
        
        public EventSourceDecorator(EventSource eventSource) {
            this.eventSource = eventSource;
        }
        
        @Override
        public void run() {
            try {
                loggerTasks.info("reading");
                Event event = eventSource.read();
                if (event != null) {
                    targetSep.onEvent(event);
                } else {
                    loggerTasks.info("no event to read");
                }
            } catch (Exception e) {
                loggerTasks.error("problem reading event source", e);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "dummyHandler");
        sepExecutor.setSleepMicros(500 * 1000);
        
        sepExecutor.registerEventSource(() -> {
            sepExecutor.loggerTasks.info("dummy read");
            return null;
        });
        
        Thread.sleep(2000);
        Future<String> futureResult = sepExecutor.submit(() -> {
            sepExecutor.loggerTasks.info("random request");
            Thread.sleep(1000);
            return "complete";
        });
        String result = futureResult.get();
        sepExecutor.loggerRequests.info("result from task:" + result);
        System.out.println("shutting down");
        sepExecutor.shutDown();
        
    }
    
}

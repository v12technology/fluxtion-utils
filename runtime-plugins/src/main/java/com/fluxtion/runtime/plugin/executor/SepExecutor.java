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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
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
    private ScheduledExecutorService executor;
    private final AtomicLong sleepMicros;
    private final Logger logger;

    public SepExecutor(EventHandler targetSep, String name) {
        logger = LoggerFactory.getLogger("SepExecutor-" + name);
        this.targetSep = targetSep;
        this.name = name;
        sleepMicros = new AtomicLong(100 * 1000);
        start();
    }

    public void registerEventSource(EventSource eventSource) {
        executor.scheduleAtFixedRate(new EventSourceDecorator(eventSource), 1, 1, TimeUnit.NANOSECONDS);
    }

    public void shutDown() {
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("failed to shutdown within 2 seconds, forcing shutdown", ex);
            executor.shutdownNow();
        }
    }

    public void setSleepMicros(long sleepMicros) {
        sleepMicros = sleepMicros < 0 ? 0 : sleepMicros;
        this.sleepMicros.lazySet(sleepMicros);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    private void start() {
        logger.info("starting");
        executor = Executors.newSingleThreadScheduledExecutor(new MyThreadFactory());
        targetSep = (EventHandler) (Event e) -> {
            System.out.println("processing event");
            logger.info("processing event");
        };
        executor.scheduleWithFixedDelay(() -> {
            logger.info("sleeping");
            long sleep = sleepMicros.get();
            if (sleep > 0) {
                LockSupport.parkNanos(sleep * 1000);
            }
        }, 1, 1, TimeUnit.MICROSECONDS);
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
            logger.info("reading");
            Event event = eventSource.read();
            if (event != null) {
                targetSep.onEvent(event);
            } else {
                logger.info("no event to read");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SepExecutor sepExecutor = new SepExecutor(EventHandler.NULL_EVENTHANDLER, "dummyHandler");
        sepExecutor.setSleepMicros(500 * 1000);
        
        
        sepExecutor.registerEventSource(() -> {
            sepExecutor.logger.info("dummy read");
            return null;
        });

        Thread.sleep(2000);
        Future<String> futureResult = sepExecutor.submit(() -> {
            System.out.println("HELP");
            sepExecutor.logger.info("random request");
            Thread.sleep(1000);
            return "complete";
        });
        String result = futureResult.get();
        sepExecutor.logger.info("result from task:" + result);
        System.out.println("shutting down");
        sepExecutor.shutDown();

    }

    public class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name);
        }

    }
    
    
    private static class MyFutureTask<T> extends FutureTask<T>{
        
        public MyFutureTask(Callable<T> callable) {
            super(callable);
        }
        
    }

}

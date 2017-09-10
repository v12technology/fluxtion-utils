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
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An executor for the a registered static event processor (SEP). The
 * SepExecutor provides the following functionality:
 *
 * <ul>
 * <li>Register a SEP for processing/scheduling
 * <li>Thread safe interaction with the SEP. All SEP processing takes place on a
 * single thread
 * <li>Tasks can be submitted for processing {@link #submit(Callable) }
 * <li>Registered {@link EventSource} are read and Events are processed by the
 * registered SEP.
 * <li>Multiple scheduling policies:
 * <ul>
 * <li>setSleepMicros : sleep between each service loop
 * <li>busySpin : no sleep between each service loop, keep a cpu 100% busy
 * <li>waitOnTaskQueue : service loop wakes when a task is submitted
 * </ul>
 * </ul>
 *
 * The default scheduling strategy is setSleepMicros with value of 100_000
 * micros (100 milliseconds)
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SepExecutor implements AsyncEventHandler {

    private final EventHandler targetSep;
    private final String name;
    private final BlockingQueue<FutureTask> queue = new ArrayBlockingQueue<>(10);
    private final BlockingQueue<Event> defaultEventQueue = new ArrayBlockingQueue<>(100);
    private final AtomicBoolean run = new AtomicBoolean(true);
    private final AtomicBoolean waitiOnTask = new AtomicBoolean(false);

    private final AtomicLong sleepMicros;
    private final Logger loggerRequests;
    private final Logger loggerTasks;

    private final ArrayList<EventSourceDecorator> sourceList;
    private EventSourceDecorator[] sourceArray;
    private Thread eventLoopThread;

    public SepExecutor(EventHandler targetSep, String name) {
        this(targetSep, name, SchedulingConfig.sleepInMillis(100));
    }

    public SepExecutor(EventHandler targetSep, String name, SchedulingConfig scheduling) {
        loggerRequests = LoggerFactory.getLogger("sep." + name + ".put");
        loggerTasks = LoggerFactory.getLogger("sep." + name + ".take");
        this.targetSep = targetSep;
        this.name = name;
        sourceList = new ArrayList<>();
        sourceArray = new EventSourceDecorator[0];
        sleepMicros = new AtomicLong(scheduling.getSleep());
        sourceList.add(new EventSourceDecorator(new DefaultEventSource()));
        sourceArray = sourceList.toArray(sourceArray);
        switch (scheduling.getStrategy()) {
            case BUSY:
                busySpin();
                break;
            case SLEEP:
                sleep(scheduling.getSleep());
                break;
            case WAIT:
                waitOnTaskQueue();
                break;
        }
        start();
    }

    public final void registerEventSource(EventSource eventSource, String name) {
        checkState();
        try {
            submit(() -> {
                loggerTasks.info("adding EventSource:" + name);
                sourceList.add(new EventSourceDecorator(eventSource));
                sourceArray = sourceList.toArray(sourceArray);
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException interruptedException) {
            loggerRequests.warn("problem adding EventSource:" + name, interruptedException);
        }
    }

    public void shutDown() {
        checkState();
        loggerRequests.info("shutting down");
        waitOnTaskQueue();
        try {
            Future<Object> submit = submit(() -> {
                run.set(false);
                queue.forEach(f -> f.cancel(true));
                queue.clear();
                return null;
            });
            submit.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            loggerRequests.error("problem shutting down event loop", ex);
        }
    }

    /**
     * Service loop will pause between each iteration. If value is &lt;= 0 this
     * is equivalent to calling {@link #busySpin() }
     *
     * @param sleepMicros the pause between loop iterations
     */
    private void setSleepMicros(long sleepMicros) {
        checkState();
        sleepMicros = sleepMicros < 0 ? 0 : sleepMicros;
        loggerRequests.info("setting setSleepMicros: {}", sleepMicros);
        this.sleepMicros.lazySet(sleepMicros);
        LockSupport.unpark(eventLoopThread);
    }

    /**
     * Service loop will pause between each iteration. If value is &lt;= 0 this
     * is equivalent to calling {@link #busySpin() }
     *
     * @param sleepMicros the pause between loop iterations
     */
    public final void sleep(long sleepMicros) {
        loggerRequests.info("scheduling: SLEEP");
        checkState();
        waitiOnTask.lazySet(false);
        setSleepMicros(sleepMicros);
    }

    /**
     * Busy spin cpu checking all {@link EventSource}'s and tasks submitted by
     * {@link #submit(Callable) }
     */
    public final void busySpin() {
        loggerRequests.info("scheduling: BUSY");
        checkState();
        waitiOnTask.lazySet(false);
        setSleepMicros(0);
    }

    /**
     * Progress will only be made when tasks are submitted, using
     * {@link  #submit}
     */
    public final void waitOnTaskQueue() {
        loggerRequests.info("scheduling: WAIT");
        checkState();
        waitiOnTask.set(true);
        setSleepMicros(0);
    }

    /**
     * Submit tasks to the registered SEP for processing. Tasks will interact
     * with the SEP asynchronously on the SEP processor thread. A {@link Future}
     * is returned encapsulating any return value, accessing via
     * {@link Future#get}
     *
     * @param <T> The return value of the task if any
     * @param task The task to interact with the SEP
     * @return The Future value of the task
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        loggerRequests.debug("submitting task");
        checkState();
        FutureTask<T> ft = new FutureTask(task);
        queue.add(ft);
        return ft;
    }
    
    @Override
    public EventHandler delegate() {
        return targetSep;
    }

    @Override
    public void onEvent(Event e) {
        try {
            defaultEventQueue.put(e);
        } catch (InterruptedException ex) {
            loggerRequests.error("unable to post event to defaultEventQueue", ex);
            throw new RuntimeException(ex);
        }
    }
    
    private void checkState() {
        if (!run.get()) {
            throw new IllegalStateException("cannot process requests, SepExecutor is stopped");
        }
    }

    private void start() {
        loggerRequests.info("start");
        eventLoopThread = new Thread(name) {
            @Override
            public void run() {
                while (run.get()) {
                    loggerTasks.trace("entering event loop");
                    //read EventSource's
                    for (EventSourceDecorator eventSource : sourceArray) {
                        eventSource.run();
                    }
                    //process commands
                    long sleep = sleepMicros.get();
                    if (sleep > 0) {
                        loggerTasks.trace("sleeping");
                        LockSupport.parkNanos(sleep * 1000);
                    }
                    FutureTask future = null;
                    if (waitiOnTask.get()) {
                        loggerTasks.trace("waiting task queue take");
                        try {
                            future = queue.take();
                        } catch (InterruptedException ex) {
                            loggerTasks.info("task queue take interrupted");
                        }
                    } else {
                        loggerTasks.trace("polling task queue");
                        future = queue.poll();
                    }
                    if (future != null) {
                        loggerTasks.trace("processing task");
                        future.run();
                    }
                }
                loggerTasks.info("exiting event loop");
            }
        };
        eventLoopThread.start();
    }

    private class DefaultEventSource implements EventSource {

        @Override
        public Event read() {
            return defaultEventQueue.poll();
        }

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
                loggerTasks.trace("reading");
                Event event = eventSource.read();
                if (event != null) {
                    targetSep.onEvent(event);
                } else {
                    loggerTasks.trace("no event to read");
                }
            } catch (Exception e) {
                loggerTasks.error("problem reading event source", e);
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SingleThreadedAsyncEventHandler implements AsyncEventHandler {

    private final EventHandler handler;

    public SingleThreadedAsyncEventHandler(EventHandler handler) {
        this.handler = handler;
    }

    @Override
    public Future submitTask(SepCallable task) {
        return new Future() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return true;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                try {
                    return task.call(handler);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return get();
            }
        };
    }

    @Override
    public void onEvent(Event e) {
        handler.onEvent(e);
    }

    @Override
    public EventHandler delegate() {
        return handler;
    }

}

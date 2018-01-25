/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.runner;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.plugin.container.server.SEPManagementEngine;
import com.fluxtion.runtime.plugin.executor.EventSource;
import com.fluxtion.runtime.plugin.executor.SepCallable;
import com.fluxtion.runtime.plugin.executor.SepExecutor;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import spark.Route;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SEPRunner {

    private final SepExecutor executor;
    private final SEPManagementEngine managementEngine;

    public SEPRunner(EventHandler handler, String name) {
        this(handler, name, -1);
    }

    public SEPRunner(EventHandler handler, String name, int managementPort) {
        executor = new SepExecutor(handler, name);
        managementEngine = new SEPManagementEngine();
        managementEngine.init(managementPort < 1 ? 4567 : managementPort);
        managementEngine.registerSep(executor, name);
    }

    public final void registerEventSource(EventSource eventSource, String name) {
        executor.registerEventSource(eventSource, name);
    }

    public void shutDown() {
        executor.shutDown();
    }

    public final void sleep(long sleepMicros) {
        executor.sleep(sleepMicros);
    }

    public final void busySpin() {
        executor.busySpin();
    }

    public final void waitOnTaskQueue() {
        executor.waitOnTaskQueue();
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public <T> Future<T> submitTask(SepCallable task) {
        return executor.submitTask(task);
    }

    public void onEvent(Event e) {
        executor.onEvent(e);
    }

    public void get(String string, Route route) {
        managementEngine.get(string, route);
    }

    public void getJson(String path, Route route) {
        managementEngine.getJson(path, route);
    }

    public void post(String path, Route route) {
        managementEngine.post(path, route);
    }
}

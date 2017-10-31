/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.lifecycle.EventHandler;

/**
 * Submit tasks to an {@link AsyncEventHandler} using SepCallable to interact
 * with an {@link EventHandler} in a thread safe manner. {@link #call(EventHandler)
 * } is invoked on the same thread events are pushed into the
 * {@link EventHandler}. Any return from the call method is passed into the
 * instance returned by submitting a SepCallable to the
 * {@link AsyncEventHandler}.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 *
 *
 * @param <V> The return type of the call method
 * @param <E> The sub-type of the EventHandler managed by the
 * {@link AsyncEventHandler}
 */
@FunctionalInterface
public interface SepCallable<V, E extends EventHandler> {

    V call(E sep) throws Exception;

}
